// React implementation of Algo UI Account Admin page
import React from 'react';
import PropTypes, { func, object } from 'prop-types';
import Moment from 'moment';
import HelpDropdown from 'shared-dropdown/HelpDropdown.jsx';
import GenericGrid from 'shared-grid/GenericGrid.jsx';
import GenericDropdown from 'shared-dropdown/GenericDropdown.jsx';
import {GenericModal} from 'shared-dialog/GenericModal.jsx';
import StorageService from 'shared-service/JsonStore.js';
import {DATA_TYPE} from 'shared-grid/Constants.js';
import {MENU_TYPE} from 'shared-dropdown/Constants.js';
import {STATUS} from 'shared-service/Constants.js';
import AlgoAccountModal from './AlgoAccountModal.jsx';

import './App.scss';

const USER_ACTION = {
	ACITVATE: 'Activate',
	DEACITVATE: 'Deactivate',
	EDIT: 'Edit',
	NEW: 'New'
},
getModification = function(account){
	return Moment(new Date(account.modifiedDate)).format('YYYY-MM-DD') + ' ' + account.modifier;
};

// List of Algo UI Accounts - main export class
class AlgoAdminAcct extends React.Component {
	static propTypes = {
		// portal top - used as context for modal dialogs
		banner: PropTypes.instanceOf(Element).isRequired,
		
		// right side of portal top - used as context for help drop-dropdown
		bannerMenuBar: PropTypes.instanceOf(Element).isRequired
	}
	
	state = {
		userAction: '',
		accountStorage: {data: [], status: {}, actions: {}}
	}
	
	// grid definition
	columns = [
		{field: 'clientName', label: 'Client Name', type: DATA_TYPE.TEXT, relativeWidth: 2 },
		{field: 'id', label: 'Account #', type: DATA_TYPE.TEXT },
		{field: 'enabled', label: 'Status', type: DATA_TYPE.BOOL, possibleValues: [
			{value: '', displayName: ''},
			{value: true, displayName: 'Active'},
			{value: false, displayName: 'Inactive'}]
		},
		{field: 'marketingGroup', label: 'Marketing Group', type: DATA_TYPE.TEXT, possibleValues: [
			{value: '', displayName: ''},
			{value: 'FGBG', displayName: 'FGBG'},
			{value: 'FCM', displayName: 'FCM'}]
		},
		{field: 'modification', label: 'Last Modified', type: DATA_TYPE.TEXT, relativeWidth: 2 },
		{field: 'actions', label: 'Actions', type: DATA_TYPE.ACTION_MENU, formatter: (item)=>{return this.actionsFormatter(item);}}
	]
	
	actionsFormatter = (rowItem) => {
		// Show enable/disable as the case may be
		let actionItems = [];
		if(rowItem.enabled){
			actionItems.push({id: USER_ACTION.DEACITVATE, label: ' Deactivate', icon: 'fa fa-user-times fa-lg', type: MENU_TYPE.LINK});
		}else{
			actionItems.push({id: USER_ACTION.ACITVATE, label: ' Activate', icon: 'fa fa-check-square fa-lg', type: MENU_TYPE.LINK});
		}
		if(rowItem.clientId != null){
			// there are client-less accounts at least in dev environments
			actionItems.push({id: USER_ACTION.EDIT, label: ' Edit', icon: 'fa fa-pencil-square-o fa-lg', type: MENU_TYPE.LINK});
		}
		const status = (this.state.accountStorage.actions || {})[rowItem.id],
			triggerClass = status === STATUS.LOADING ? 'is-loading' : '',
			iconClass = 'fa ' + (status === STATUS.FAILED ? 'fa-exclamation-triangle' : 'fa-angle-down');
		return (
			<GenericDropdown items={actionItems} onItemClick={(actionItem)=>{this.handleMenuClick(actionItem, rowItem); }}
				iconClass={iconClass} triggerLabel="Action" triggerClass={triggerClass}/>
		);

	}
	
	// dropdown click dispatcher
	handleMenuClick = (actionItem, rowItem)=>{
		this.changeAccountStatusConfirm(actionItem.id, rowItem);
	}

	changeAccountStatusConfirm = (userAction, rowItem)=>{
		this.setState(({accountStorage})=>{
			const actions = accountStorage.actions,
				id = rowItem.id;
			actions[id] = STATUS.WAIT4INPUT;
			return {accountStorage: {data: accountStorage.data, actions: actions, status: accountStorage.status},
				userAction: userAction};
		});
	}
	
	constructor(props) {
		super(props);
		this.accountService = new StorageService({target: 'algoadmin/account',
			idProperty: 'id',
			derived: {modification: getModification},
			updateStorage: this.updateAccountStorage});
	}

	// storage setter passed to service
	updateAccountStorage = ({fn, op}) => {
		this.setState(({accountStorage}) => {
			const storage = fn(accountStorage);
			return {accountStorage: storage, userAction: ''};
		});
	}

	componentDidMount(){		
	 	this.accountService.load();
	}
	
	render(){
		const storage = this.state.accountStorage,
			userAction = this.state.userAction;
		let confirmModal = null;
		if(userAction){
			if(userAction === USER_ACTION.NEW){
				confirmModal = <AlgoAccountModal rootNode={this.props.banner} onClose={this.closeConfirmationModal}
					onAccountUpdate={this.updateAccounts} />;
			}else{
				const actionAccount = this.findActionAccount();
				if(actionAccount){
					if(userAction === USER_ACTION.EDIT){
						confirmModal = <AlgoAccountModal rootNode={this.props.banner} onClose={this.closeConfirmationModal}
							onAccountUpdate={this.updateAccounts} account={actionAccount} />;	
					}else{
						const msg = 'You are about to ' + userAction + ' ' + actionAccount.clientName + ' Account Number: ' + actionAccount.id;
						confirmModal = <GenericModal buttons={this.userActionModalButtons()} rootNode={this.props.banner} className={'confirmation-modal'} title={'Warning'}
								onClose={this.closeConfirmationModal} >
								{msg}
							</GenericModal>;	
					}
				}
			}
		}
		return (
			<div>
				<a className={'button is-outlined is-info'} onClick={this.onAddButtonClick} title="Add Client Accounts" style={{margin: 15}} >
	 				<span className="icon"><i className="fa fa-plus"></i></span>
					<span> New</span>
				</a>
				<div style={{position: 'absolute', top: 50, bottom: 0, right: 0, left: 0}}>
					<GenericGrid columns={this.columns} hasHeader={true} hasInlineFilter={true}
						message={storage} />
				</div>
				<HelpDropdown rootNode={this.props.bannerMenuBar} />
				{confirmModal}
			</div>
		);
	}
	
	findActionAccount = () => {
		const storage = this.state.accountStorage,
			actionAccount = storage.data.find(({id})=>{
				return storage.actions[id] === STATUS.WAIT4INPUT;
			});
		return actionAccount;
	}
	
	closeConfirmationModal = () => {
		const actionAccount = this.findActionAccount();
		if(actionAccount){
			this.setState(({accountStorage}) => {
				const newAccountStorage = Object.assign({}, accountStorage);
				delete newAccountStorage.actions[actionAccount.id];
				return {accountStorage : newAccountStorage, userAction: ''};
			});
		}else{
			this.setState({userAction: ''});
		}
	}
	
	userActionModalButtons = () => {
		const userAction = this.state.userAction;
		return [
			{name: 'close', label: 'Close', className: 'is-outlined', handler: this.props.onClose, disabled: false },
			{name: 'update', label: userAction, className: 'is-outlined is-info', handler: this.handleUpdate }
		];
	}
	
	handleUpdate = ()=>{
		const userAction = this.state.userAction,
			actionAccount = this.findActionAccount();
		let enabled = null;
		if(userAction === USER_ACTION.ACITVATE){
			enabled = true;
		}else if(userAction === USER_ACTION.DEACITVATE){
			enabled = false;
		}
		if(enabled !== null){
			actionAccount.enabled = enabled;
			this.accountService.put(actionAccount);
		}
	}

	updateAccounts = (accounts) => {
		for(let i = 0; i < accounts.length; i++){
			this.accountService.addNotify(accounts[i]);
		}
	}
	
	onAddButtonClick = () => {
		this.setState({userAction: USER_ACTION.NEW});
	}
}

export default AlgoAdminAcct;
