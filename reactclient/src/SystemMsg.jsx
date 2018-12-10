import React from 'react';
import PropTypes from 'prop-types';
import axios from 'axios';
import HelpDropdown from 'shared-dropdown/HelpDropdown.jsx';
import {GenericModal} from 'shared-dialog/GenericModal.jsx';
import StorageService from 'shared-service/JsonStore.js';
import {STATUS, OPERATION} from 'shared-service/Constants.js';

import './App.scss';

const apps = [
	{application: 'ExRay', displayName: 'EX-RAY'},
	{application: 'CSA', displayName: 'CSA'},
	{application: 'AlgoUI', displayName: 'Commodore'},
	{application: 'UserAdmin', displayName: 'System'}
],
levels = [
	{level: 0, displayName: 'None'},
	{level: 1, displayName: 'Informational'},
	{level: 2, displayName: 'Warning'},
	{level: 3, displayName: 'Error'}
];

class SystemMsg extends React.Component {
	static propTypes = {
		// portal top - used as context for modal dialogs
		banner: PropTypes.instanceOf(Element).isRequired,
		
		// right side of portal top - used as context for help drop-dropdown
		bannerMenuBar: PropTypes.instanceOf(Element).isRequired
	}
	state = {
		// what is currently selected in application dropdown
		currentApplication: apps[0].application,
		
		systemStorage: {data: [], status: {load: STATUS.LOADING}, actions: {}}
	}
	
	constructor(props) {
		super(props);
		this.systemService = new StorageService({target: 'sysmessage/message', idProperty: 'application',
			updateStorage: this.updateSystemStorage})
	}
	
	// storage setter passed to service
	updateSystemStorage = ({fn, op}) => {
		this.setState(({systemStorage}) => {
			const oldAddStatus = systemStorage.status.add;
			let storage = fn(systemStorage);
			if(op === OPERATION.LOAD){
				storage = this.transformInitialLoad(storage);
			}else if(op === OPERATION.ADD){
				if(oldAddStatus === STATUS.LOADING && !storage.status.add){
					// successful update: alter status for confirmation popup
					storage.status.add = STATUS.READY;
				}
			}
			return {systemStorage: storage};
		});
	}
	
	// Normalize server response to include a (possibly empty) message for each known application
	transformInitialLoad = (storage) => {
		if(!storage.status.load){
			const inData = storage.data;
			storage.data = apps.map(({application}) => {
				const inMsg = this.getCurrentMsg(inData, application);
				return inMsg ? inMsg : {application: application, message: '', level: 0};
			});
		}
		return storage;
	}
	
	componentDidMount(){		
	 	this.systemService.load();
	}
	
	render(){
		let _message = null, _application = null, _level = null, _update = null, _clear = null, _modal = null, _help = null;
		const loadStatus = this.state.systemStorage.status.load;
		
		if(loadStatus === STATUS.LOADING){
			_help = <p>Loading&hellip;</p>;
		}else if(loadStatus === STATUS.FAILED){
			_help = <p>Load Failed</p>;
		}else{
			const currentApplication = this.state.currentApplication,
				currentMsg = this.getCurrentMsg(),
				submitStatus = this.state.systemStorage.status.add,
				submitName = currentMsg.level == '0' ? 'clear' : 'update',
				isButtonLoading = (buttonName) => {
					if(submitStatus === STATUS.LOADING){
						return submitName === buttonName;
					}
					return false;
				},
				isUpdateDisabled = currentMsg.message.trim() === '' || currentMsg.level == '0';
					
			_message = <textarea className="textarea" placeholder="Enter Text&hellip;" name="message" value={currentMsg.message} onChange={this.handleMessageChange} />;
			_application = (<div className="select is-fullwidth"><select name="application"  value={currentApplication} onChange={this.handleAppSelect}>
			{apps.map(({application, displayName}) => <option key={application} value={application}>{displayName}</option>)}
			</select></div>);
			_level = (<div className="select is-fullwidth"><select name="level"  value={currentMsg.level} onChange={this.handleLevelSelect}>
			{levels.map(({level, displayName}) => <option key={level} value={level}>{displayName}</option>)}
			</select></div>);
			_update = (<div className="card-footer-item">
				<button className={'button is-success is-outlined' + (isButtonLoading('update') ? ' is-loading' : '')} name="update" disabled={isUpdateDisabled} onClick={this.handleSubmit}>Update Message</button>
				</div>);
			_clear = (<div className="card-footer-item">
				<button className={'button is-danger is-outlined' + (isButtonLoading('clear') ? ' is-loading' : '')} name="clear" onClick={this.handleSubmit}>Delete Message</button>
				</div>);
			if(submitStatus === STATUS.READY){
				const title = 'Update Success',
					msg = submitName === 'update' ? 'System message updated and will be displayed to all the appropriate users.'
					: 'System message deleted.';
				_modal = <GenericModal buttons={[]} rootNode={this.props.banner} className={'confirmation-modal'} title={title}
					onClose={this.closeModal} >
					{msg}
				</GenericModal>;	
			}else if(submitStatus === STATUS.FAILED){
				_help = (<p className="help is-danger">{'Update failed'}</p>);
			}
		}
		
		return(
		<div className="card" style={{margin: '100px auto 0', maxWidth: 400}}>
			<header className="card-header">
				<p className="card-header-title">System Message</p>
			</header>
			<div className="card-content">
				<div className="field">
				{_message}
				{_help}
				</div>
				<div className="field">
				{_application}
				</div>
				<div className="field">
				{_level}
				</div>
			</div>
			<footer className="card-footer">
			{_update}{_clear}
			</footer>
			{_modal}
			<HelpDropdown rootNode={this.props.bannerMenuBar} />		
		</div>
		);
	}
	
	// Retrieves message from list by key.
	// List defaults to storage data and key defaults to selected application
	getCurrentMsg = (inList, inCurrentApplication) => {
		const list = inList || this.state.systemStorage.data,
			currentApplication = inCurrentApplication || this.state.currentApplication;
		return list.find(({application}) => {return application === currentApplication});
	}
	
	// Submit buttons click handler
	handleSubmit = ({target}) => {
		const name = target.name;
		if(name === 'clear'){
			this.systemService.addNotify({application: this.state.currentApplication, level: 0, message: ''});
		}
		const msg = this.getCurrentMsg(),
			payload = Object.assign({author: FI.loginUser.lid, created: (new Date()).getTime()}, msg);
		this.systemService.add(payload);
	}
	
	// Success acknowledge handler
	closeModal = () => {
		this.setState(({systemStorage})=>{
			delete systemStorage.status.add;
			return {systemStorage: systemStorage};
		});
	}
	
	// App selection handler
	handleAppSelect = ({target}) => {
		const application = target.value;
		this.setState({currentApplication: application});
	}
	
	// Level selection handler. Selecting level 0 clears message text
	handleLevelSelect= ({target}) => {
		const level = target.value,
			msg = {level: level, application: this.state.currentApplication};
		if(level == 0){
			msg.message = '';
		}
		this.systemService.addNotify(msg);
	}
	
	// Message text update handler
	handleMessageChange= ({target}) => {
		const message = target.value;
		this.systemService.addNotify({message: message, application: this.state.currentApplication});
	}
}
export default SystemMsg;
