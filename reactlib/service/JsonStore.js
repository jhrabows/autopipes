// Service component modeled after RequestMemory and JsonRest dstores,
// designed to work with React components.
// Conceptually it is a Class which wraps an Object array (like RequestMemory)
// and implements REST calls to manage array data.
// REST calls use GET/POST with actual Verb encoded in the HTTP header.
// This class also keeps track of states that active REST requests are currently in
// 
import axios from 'axios';
import {STATUS, OPERATION} from './Constants.js';

// returns passed id prepended with '/' unless empty
const PATH = function(id){ return id ? '/' + id : ''; },
//Adds derived information directly to a data item
transformItem = function(item, derived){
	for(let key in (derived || {})){
		item[key] = derived[key](item);
	}
},
// merges passed object with the data array. Also performs derived substitution
updateStorageData = function(data, updateItem, idProperty, derived){
	const id = updateItem[idProperty],
		currentItem = data.find(function(item){
		return item[idProperty] === id;
	});
	transformItem(updateItem, derived);
	if(currentItem){
		// update existing
		Object.assign(currentItem, updateItem);
	}else{
		// add new
		data.push(updateItem);
	}
},
// removes item with matching id from data array
deleteStorageData = function(data, id, idProperty){
	return data.filter((dataItem)=>{return dataItem[idProperty] !== id; });
},
// Adds/removes operation status. For specified ID the status pertains to
// the item with that ID. Otherwise it is a global status.
updateStorageOperationStatus = function(operation, storage, id, operationStatus){
	const actions = storage.actions || {},
		status = storage.status || {},
		globalOperation = operation === OPERATION.ADD || operation === OPERATION.LOAD;
	if(operationStatus){
		if(globalOperation){
			status[operation] = operationStatus;
		}else{
			actions[id] = operationStatus;
		}
	}else{
		if(globalOperation){
			delete status[operation];
		}else{
			delete actions[id];
		}
	}
	return {data: storage.data, status: status, actions: actions};
},
// Removes derived information (if present) from a shallow copy of a data item and returns that copy.
cleanItem = function(item, derived){
	let ret = item;
	if(derived && Object.keys(derived).length > 0){
		ret = Object.assign({}, item);
		for(let key in derived){
			delete ret[key];
		}
	}
	return ret;
};

class JsonStore {
	// Following React, we save args in props member. The following props are supported:
	// target - actually - service name - but we follow terminology of RequestMemory
	// idProperty - name of the primary key - same meaning as in dstore
	// derived - name/function Hash where the function is given a data item.
	// The result is identical to addingg a calculed member to the item - server side. 
	constructor(args){
		this.props = args;
	}
	
	// Prepares URL to be used by a REST call.
	// suffix, if present, is appended to the 'target' prop (service name for this store)
	serviceUrl = (args) => {
		let target = this.props.target,
			_suffix = args ? args.path : [],
			ret = [];
		if(!Array.isArray(target)){
			target = [target];
		}
		if(!Array.isArray(_suffix)){
			_suffix = [_suffix];
		}
		for(let i = 0; i < target.length; i++){
			ret.push(target[i]);
			if(i < _suffix.length){
				ret.push(_suffix[i]);
			}
		}
		return this.prepareServiceURL(ret.join('/'));
	}

	// Rule (designed to be overwritten) which produces final REST URL.
	// For example, if the service is deployed in a servlet container,
	// the rule prepends servlet context-string (e.g. /service/) to the service name.
	// Here, serviceName is assumed to include optional suffix.
	prepareServiceURL = (serviceName) => {
		const BASE_URL = '/service'
		return BASE_URL + '/' + serviceName;
	}
	
	// delete item from list
	del = (item) => {
		const postHeaders = {'X-HTTP-Method-Override': 'DELETE'},
			idProperty = this.props.idProperty,
			id = item[idProperty],
			operation = OPERATION.DEL;
		axios.post(this.serviceUrl(PATH(id)), {}, {headers: postHeaders}).then(res=>{
			this.props.updateStorage({fn: (storage)=>{
				const data = deleteStorageData(storage.data, id, idProperty);
				return updateStorageOperationStatus(operation, {data: data, status: storage.status, actions: storage.actions}, id);
			}, op: operation});
		}, ({response})=>{
			this.props.updateStorage({fn: (storage)=>{
					return updateStorageOperationStatus(operation, storage, id, STATUS.FAILED);
					}, op: operation
			});
		});
		this.props.updateStorage({fn: (storage)=>{
				return updateStorageOperationStatus(operation, storage, id, STATUS.LOADING);
				}, op: operation
		});
	}
	//Adds derived information to an array of data items
	_transformData = (data) => {
		const derived = this.props.derived;
		if(derived){
			for(let i = 0; i < data.length; i++){
				const item = data[i];
				transformItem(item, derived);
			}
		}
	}
	
	// Retrieves all list data from base url
	// (possibly appended with passed path-variales and query parameters)
	load = (args) => {
		const operation = OPERATION.LOAD;
		/*
			path = (args || {}).path || [],
			params = (args || {}).params || {},
			paramList = [];
		for(let key in params){
			const paramValues = params[key];
			for(let i = 0; i < paramValues.length; i++){
				paramList.push(key + '=' + paramValues[i]);
			}
		}
		const url = (path.length > 0 ? '/' : '') + path.join('/') + (paramList.length > 0 ? ('?' + paramList.join('&')) : '');
		*/
		axios.get(this.serviceUrl(args)).then(res => {
			const data = res ? (res.data || []) : [];
			this._transformData(data);
			this.props.updateStorage({fn: (storage)=>{
				return updateStorageOperationStatus(operation, {data: data, status: storage.status, actions: storage.actions}); },
				op: operation});
		}, err => {
			this.props.updateStorage({fn: (storage)=>{
				return updateStorageOperationStatus(operation, {data: [], status: storage.status, actions: storage.actions}, null, STATUS.FAILED); },
				op: operation});
		});		
		this.props.updateStorage({fn: (storage)=>{
			return updateStorageOperationStatus(operation, {data: storage.data || [], status: storage.status, actions: storage.actions}, null, STATUS.LOADING); },
			op: operation});
	}
	
	// partial update of a list item
	patch = (payload) => {
		this._put(payload, OPERATION.PATCH);
	}
	
	// full update of a list item
	put = (payload) => {
		this._put(payload, OPERATION.PUT);
	}
	
	// variant of 'del' operation which only modifies local array
	delNotify = (id) => {
		this.props.updateStorage({fn: (storage)=>{
			const idProperty = this.props.idProperty,
				data = deleteStorageData(storage.data, id, idProperty);
			return {data: data, status: storage.status, actions: storage.actions};
		}, op: OPERATION.DEL_NOTIFY});
	}
	// variant of 'add' operation which only modifies local array
	addNotify = (payload) => {
		this.props.updateStorage({fn: (storage)=>{
			const idProperty = this.props.idProperty,
				data = storage.data,
				derived = this.props.derived;
			
			updateStorageData(data, payload, idProperty, derived);
			return {data: data, status: storage.status, actions: storage.actions};
		}, op: OPERATION.ADD_NOTIFY});
	}
	
	// adding new item to list
	add = (payload) => {
		this._put(payload, OPERATION.ADD);
	}
	
	_put = (payload, operation) => {
		const idProperty = this.props.idProperty,
			derived = this.props.derived,
			axiosPayload = cleanItem(payload, derived),
			id = payload[idProperty];
		let verb = '';

		if(operation === OPERATION.PATCH){
			verb = 'PATCH';
		}else if(operation === OPERATION.ADD || operation === OPERATION.PUT ){
			if(id){
				verb = 'PUT';
			}
		}
		const postHeaders = {'X-HTTP-Method-Override': verb};
			
		axios.post(this.serviceUrl(PATH(id)), axiosPayload, verb ? {headers: postHeaders} : {}).then(res=>{
			const resItem = res.data;
			this.props.updateStorage({fn: (storage)=>{
				const data = storage.data;
				
				updateStorageData(data, resItem, idProperty, derived);
				return updateStorageOperationStatus(operation, {data: data, status: storage.status, actions: storage.actions}, id);
			}, op: operation});
		}, ({response})=>{
			this.props.updateStorage({fn:(storage)=>{return updateStorageOperationStatus(operation, storage, id, STATUS.FAILED);},
				op: operation});
		});
		
		this.props.updateStorage({fn: (storage)=>{return updateStorageOperationStatus(operation, storage, id, STATUS.LOADING);},
			op: operation});
	}
}

export default JsonStore;