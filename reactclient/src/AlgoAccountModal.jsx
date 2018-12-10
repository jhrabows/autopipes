// React implementation of Algo Account dialog
import React from 'react';
import PropTypes, { func, string, object } from 'prop-types';
import Select from 'react-select';
import StorageService from 'shared-service/JsonStore.js';
import {STATUS, OPERATION} from 'shared-service/Constants.js';
import {DATA_TYPE} from 'shared-grid/Constants.js';
import {GenericModal} from 'shared-dialog/GenericModal.jsx';
import GenericGrid from 'shared-grid/GenericGrid.jsx';

// styling of react-select component
const DBL_CLICK_TIMEOUT = 250,
selectStyles = {
	control: styles => ({...styles, backgroundColor: 'black', minHeight: 20, lineHeight: 1, width: 300}),
	singleValue: (styles) => ({ ...styles, color: 'white' }),
	option: (styles, {isSelected}) => {
		return {
			width: 300,
			backgroundColor: isSelected ? 'white' : 'black',
			color:  isSelected ? 'black' : 'white'
		};
	}
},
MKT_GROUP_VALUES=[
	'FGBG',
	'FCM'
],
serializeMask = function(strategyEnabled){
	const values = Object.keys(strategyEnabled || {}).sort().map((strategy)=>{return strategyEnabled[strategy]; });
	return JSON.stringify(values);
},
accountHasChanged = function(acct){
	return acct.marketingGroup !== acct.originalMarketingGroup
		|| serializeMask(acct.strategyEnabled) !== serializeMask(acct.originalStrategyEnabled);
};

// Responsible for selecting marketing goup
class MarketingGroupSelector extends React.Component {
	static propTypes = {
		value: string.isRequired,
		onChange: func.isRequired
	}
	render(){
		const values = (this.props.value ? [] : ['']).concat(MKT_GROUP_VALUES);
		return(
			<div className="select">
				<select value={this.props.value} onChange={this.props.onChange}>
					{values.map(
						(value)=>{
							return (<option key={value} value={value}>{value ? value : 'Select Group'}</option>);
						}
					)}
				</select>
			</div>
		);
	}
}

// Responsible for collecting service mask info from the user and passing it up to the main component
class StrategyMaskSelector extends React.Component {
		
	static propTypes = {
		value: object.isRequired,
		onChange: func.isRequired
	}
	
	render(){
		const strategyList = this.buildStrategyList();

		return (<div>
			{strategyList.map(({strategyId, strategyName, isEnabled})=>{
				return (
				<div className="field"><div className="control">
					<label className="checkbox" style={{margin:0}}>
						<input key={strategyId} type="checkbox" name={strategyId} checked={isEnabled} onChange={this.handleCheckbox} />
						{strategyName}
					</label>
				</div></div>
				);
			})}
			</div>);
	}
	
	handleCheckbox = ({target}) => {
		const newStrategyEnabled = Object.assign({}, this.props.value, {[target.name]: target.checked});
		this.props.onChange(newStrategyEnabled);
	}
		
	buildStrategyList = () => {
		const retList = [],
			enabledMap = this.props.value;
		for(let strategyId in FI.strategies){
			const strategyName = FI.strategies[strategyId],
				isEnabled = enabledMap[strategyId];
			retList.push({strategyId: strategyId, strategyName: strategyName, isEnabled: isEnabled });
		}
		return retList;
	}
}

// Dialog for adding/editing an algo account - main export class
class AlgoAccountModal extends React.Component {

	static propTypes = {
		rootNode: PropTypes.instanceOf(Element).isRequired,
		onClose: func.isRequired
	}
	
	state = {
		clientStorage: {data: [], status: {}, actions: {}},
		selectedClientId: null, // client id selected from the client's list
		clientAccountStorage: {data: [], status: {}, actions: {}},
		clientAccountId: null, // account id selected from available client accounts grid
		currentAccountId: null // account id selected from mapped accounts grid
	}
	
	availColumns = [
		{field: 'clientId', label: 'Client Id', type: DATA_TYPE.TEXT},
		{field: 'id', label: 'Account #', type: DATA_TYPE.TEXT },
		{field: 'name', label: 'Account Name', type: DATA_TYPE.TEXT, relativeWidth: 2 }
	]
	
	sourceCounter = 1
	
	updateSource = () => {
		this.sourceCounter++;
	}
	
	constructor(props) {
		super(props);
		this.clientService = new StorageService({target: 'accounts/getClients',
			updateStorage: this.updateClientStorage });
		this.accountService = new StorageService({target: 'accounts/getAlgoAccounts', idProperty: 'id',
			updateStorage: this.updateAccountStorage,
			derived: {
				originalMarketingGroup: (item) => { return item.marketingGroup; },
				originalStrategyEnabled: (item) => { return item.strategyEnabled; }
			}
		});
		this.selColumns = this.availColumns.concat([
			{field: 'enabled', label: 'Status', type: DATA_TYPE.BOOL, possibleValues: [
				{value: true, displayName: 'Active'},
				{value: false, displayName: 'Inactive'}]
			}
		]);
	}

	// storage setter passed to client service
	updateClientStorage = ({fn, op}) => {
		this.setState(({clientStorage}) => {
			const storage = fn(clientStorage);
			
			storage.data = storage.data.map(({id, name})=>{ return {value: id, label: name}; });
			return {clientStorage: storage};
		});
	}
	
	// storage setter passed to account service
	updateAccountStorage = ({fn, op}) => {
		this.setState(({clientAccountStorage, currentAccountId, clientAccountId}) => {
			const storage = fn(clientAccountStorage),
				currentAccount = this.findAccountById(currentAccountId),
				clientAccount = this.findAccountById(clientAccountId);
			let newCurrentAccountId = null,
				newClientAccountId = null;
			if(currentAccount && currentAccount.marketingGroup !== null){
				// preserve existing selection if possible
				newCurrentAccountId = currentAccountId;
			}else{
				// preselect first mapped account
				const mappedAccounts = storage.data.filter(({marketingGroup})=>{return marketingGroup != null;});
				if(mappedAccounts.length > 0){
					newCurrentAccountId = mappedAccounts[0].id;
				}
			}
			if(clientAccount && clientAccount.marketingGroup === null){
				// preserve existing selection if possible
				newClientAccountId = clientAccountId;
			}else{
				// preselect first available account
				const availableAccounts = storage.data.filter(({marketingGroup})=>{return marketingGroup == null;});
				if(availableAccounts.length > 0){
					newClientAccountId = availableAccounts[0].id;
				}
			}
			if(op === OPERATION.PUT && !Object.keys(storage.actions).length){
				setTimeout(this.onSaveSuccess, 0);
			}
			return {clientAccountStorage: storage, currentAccountId: newCurrentAccountId, clientAccountId: newClientAccountId};
		});
	}
	
	componentDidMount(){
		if(this.props.account){
			 this.accountService.load({params: {clientIds: [this.props.account.clientId]}});
		}else{
	 		this.clientService.load();
		}
	}
	
	modalButtons = (enableSave, saveError) => {
		const saveLabel = saveError ? <span><i className="fa fa-exclamation-triangle" />Save</span> : 'Save';
		return [
			{name: 'close', label: 'Close', className: 'is-outlined', handler: this.props.onClose, disabled: false },
			{name: 'save', label: saveLabel, className: 'is-outlined is-info', handler: this.handleSave, disabled: !enableSave }
		];
	}
	
	saveError = () => {
		const actions = this.state.clientAccountStorage.actions;
		for(let id in actions){
			if(actions[id] === STATUS.FAILED){
				return true;
			}
		}
		return false;
	}

	render(){
		const clientLoadStatus = this.state.clientStorage.status.load,
			currentAccountId = this.state.currentAccountId,
			errorIcon=(clientLoadStatus === STATUS.FAILED ? <i className="fa fa-exclamation-triangle" /> : null),
			enableSave = this.accountsChanged() && !this.findUnfinishedAccount(),
			saveError = this.saveError(),
			title = this.props.account ? ('Editing ' + (this.props.account.clientName || 'Unknown')) : 'Adding New Algo Account';
		let mktGroupSelector = null, strategyMaskSelector = null, clientsPanel = null, allCheckbox = null;
		if(currentAccountId){
			const currentAccount = this.findAccountById(currentAccountId);
			mktGroupSelector = <MarketingGroupSelector value={currentAccount.marketingGroup} onChange={this.onMktGroupChange}  />;
			strategyMaskSelector = <StrategyMaskSelector value={currentAccount.strategyEnabled} onChange={this.onStrategyMaskChange}  />;
			allCheckbox = (<label className="checkbox">
					<input  type="checkbox" name="ALL" onChange={this.handleAllCheckbox} />Select All</label>);
		}
		if(!this.props.account){
			clientsPanel = (
				<div style={{position: 'relative'}} className="clients-panel">
					<label style={{position:'absolute', left:0, top: 10}}>
						Client Names:{errorIcon}
					</label>
					<div style={{marginLeft: 100, width: 300}}>
						<Select options={this.state.clientStorage.data}
							placeholder='Select Client'
							isLoading={clientLoadStatus === STATUS.LOADING}
							onChange={this.onClientSelection}
							styles={selectStyles} />
					</div>
				</div>
			);
		}
		return (<GenericModal buttons={this.modalButtons(enableSave, saveError)} className={'new-account-modal'}
				title = {title}
				rootNode={this.props.rootNode} onClose={this.props.onClose}>
				{clientsPanel}
			<div className="accounts-panel" style={{minHeight: 350, position: 'relative'}}>
				<div className="availableAccounts" style={{position: 'absolute', height: '100%', left: 0, width: 'calc(50% - 82px)', paddingTop: 20}}>
					<div className="panel-title"><label>Client Accounts</label></div>
					<GenericGrid columns={this.availColumns} hasHeader={true} hasInlineFilter={false}
						onDoubleClick={this.availDoubleClick}
						onClick={this.availableClick}
						rowClassName={this.availableRowClassName}
						message={{...this.state.clientAccountStorage, source: this.sourceCounter}} columnFilters={
							{marketingGroup: (item)=>{return item.marketingGroup === null;}}
						} />
				</div>
				<a className="button" onClick={this.moveRight} style={{position:'absolute', left: 'calc(50% - 72px)', top: 'calc(50% - 15px)' }}>
					<i className="fa fa-caret-right fa-lg" />
				</a>
				<a className="button" onClick={this.moveLeft} style={{position:'absolute', left: 'calc(50% - 72px)', top: 'calc(50% + 15px)' }}>
					<i className="fa fa-caret-left fa-lg" />
				</a>
				<div className="selectedAccounts" style={{position: 'absolute', height: '100%', left: 'calc(50% - 40px)', width: 'calc(50% - 150px)', paddingTop: 20}}>
					<div className="panel-title"><label>Mapped Accounts</label></div>
					<GenericGrid columns={this.selColumns} hasHeader={true} hasInlineFilter={false}
						onDoubleClick={this.mappedDoubleClick}
						onClick={this.mappedClick}
						rowClassName={this.mappedRowClassName}
						message={{...this.state.clientAccountStorage, source: this.sourceCounter}} columnFilters={
							{marketingGroup: (item)=>{return item.marketingGroup !== null;}}
						} />
				</div>
				<div className="currentAccount" style={{position: 'absolute', height: '100%', right: 0, width: 192, paddingTop: 20}}>
					<div className="panel-title"><label style={{marginLeft: 20}}>Available Algos & Mkt Group</label></div>
					<div className="currentAccountHeader">
						{allCheckbox}
						{mktGroupSelector}
					</div>
					{strategyMaskSelector}
				</div>
			</div>
			
		</GenericModal>);
	}
	
	availDoubleClick = (e, {rowItem})=>{
		this.updateSource();
		this.accountService.addNotify({
			id: rowItem.id, marketingGroup: ''
		});
	}
	moveRight = () =>{
		const clientAccountId = this.state.clientAccountId;
		if(clientAccountId){
			this.updateSource();
			this.accountService.addNotify({
				id: clientAccountId, marketingGroup: ''
			});
		}
	}
	
	moveLeft = () => {
		const currentAccountId = this.state.currentAccountId;
		if(currentAccountId){
			const currentAccount = this.findAccountById(currentAccountId);
			if(currentAccount.enabled === null){
				this.updateSource();
				this.accountService.addNotify({
					id: currentAccountId, marketingGroup: null
				});
			}
		}
	}
	
	mappedDoubleClick = (e, {rowItem})=>{
		if(rowItem.enabled === null){
			this.updateSource();
			// this mapping can be unmapped since it has not been saved yet
			this.accountService.addNotify({
				id: rowItem.id, marketingGroup: null
			});
		}
	}
		
	// if this call is a by-product of double click we allow time for the row to be removed from the  list first
	mappedClick = (e, {rowItem})=>{
		setTimeout(this.selectMappedRow, DBL_CLICK_TIMEOUT, rowItem.id);
	}
	availableClick = (e, {rowItem})=>{
		setTimeout(this.selectAvailableRow, DBL_CLICK_TIMEOUT, rowItem.id);
	}
	
	selectMappedRow = (accountId) => {
		const account = this.findAccountById(accountId);
		if(account.marketingGroup !== null){
			this.setState({currentAccountId: accountId});
		}
	}

	selectAvailableRow = (accountId) => {
		const account = this.findAccountById(accountId);
		if(account.marketingGroup == null){
			this.setState({clientAccountId: accountId});
		}
	}
	
	mappedRowClassName = ({rowItem}) => {
		return rowItem.id === this.state.currentAccountId ? 'selected' : '';
	}
	
	availableRowClassName = ({rowItem}) => {
		return rowItem.id === this.state.clientAccountId ? 'selected' : '';
	}
	
	onClientSelection = (data, action) => {
	 	this.accountService.load({params: {clientIds: [data.value]}});
	}
	
	onMktGroupChange = ({target}) => {
		this.accountService.addNotify({
			id: this.state.currentAccountId, marketingGroup: target.value
		});
	}
	
	onStrategyMaskChange = (newStrategyEnabled) => {
		this.accountService.addNotify({
			id: this.state.currentAccountId, strategyEnabled: newStrategyEnabled
		});
	}
	
	handleSave = () => {
		const modifiedAccounts = this.findAccountsToUpdate();
		for(let i = 0; i < modifiedAccounts.length; i++){
			this.accountService.put(modifiedAccounts[i]);
		}
	}
	
	handleAllCheckbox = ({target}) => {
		const value = target.checked,
			currentAccountId = this.state.currentAccountId,
			currentAccount = this.findAccountById(currentAccountId),
			newStrategyEnabled = Object.assign({}, currentAccount.strategyEnabled);
		for(let strategy in newStrategyEnabled){
			newStrategyEnabled[strategy] = value;
		}
		this.onStrategyMaskChange(newStrategyEnabled);
	}
	
	onSaveSuccess = () => {
		const mapped = this.findMappedAccounts();
		this.props.onAccountUpdate(mapped);
	}

	findAccountsToUpdate = () => {
		return this.state.clientAccountStorage.data.filter((acct)=>{
			return accountHasChanged(acct) && acct.marketingGroup;
		});
	}
	
	accountsChanged = () => {
		return this.findAccountsToUpdate().length > 0;
	}

	findMappedAccounts = () => {
		return this.state.clientAccountStorage.data.filter((acct)=>{
			return !!acct.marketingGroup;
		});
	}
	
	findUnfinishedAccount = () => {
		return this.state.clientAccountStorage.data.find((acct)=>{
			return acct.marketingGroup === '';
		});
	}
	
	findAccountById = (id) => {
		return id ? this.state.clientAccountStorage.data.find((acct)=>{ return acct.id === id; }) : null;
	}
}

export default AlgoAccountModal;