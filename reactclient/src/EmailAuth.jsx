// React implementation of Email Authorisation page
import React from 'react';
import axios from 'axios';
import PropTypes, { func } from 'prop-types';
import HelpDropdown from 'shared-dropdown/HelpDropdown.jsx';
import GenericGrid from 'shared-grid/GenericGrid.jsx';
import GenericDropdown from 'shared-dropdown/GenericDropdown.jsx';
import {GenericModal} from 'shared-dialog/GenericModal.jsx';
import StorageService from 'shared-service/JsonStore.js';
import {STATUS} from 'shared-service/Constants.js';
import {DATA_TYPE} from 'shared-grid/Constants.js';
import {MENU_TYPE} from 'shared-dropdown/Constants.js';

import './App.scss';

const MAX_DOMAIN_NAME = 100,
	impersonation = FI.impersonationMode && FI.impersonationMode(),
	highlightText = GenericGrid.highlight;

// private dialog responsible for collecting domain name from the user and passing it up to the main component
class NewExtensionModal extends React.Component {
	state = {
		domainName: ''
	}
	
	modalButtons = () => {
		return [
			{name: 'close', label: 'Close', className: 'is-outlined', handler: this.props.onClose, disabled: false },
			{name: 'save', label: 'Save', className: 'is-outlined is-info', handler: this.handleSave, disabled: !this.input || !this.input.validity.valid }
		];
	}
	
	static propTypes = {
		rootNode: PropTypes.instanceOf(Element).isRequired,
		onNewExtension: func.isRequired,
		onClose: func.isRequired
	}
	
	input = null
	
	render(){
		return (<GenericModal buttons={this.modalButtons()} className={'domain-new confirmation-modal'} title = 'New Extension' rootNode={this.props.rootNode} onClose={this.props.onClose}>
			<div className="field has-addons field-domain-name">
				<p className="control">
					<a className="button is-static is-outlined is-info">@</a>
				</p>
				<p className="control">
				<input className="input " name="domainName" placeholder="Enter Name" onChange={this.handleDomainName} ref = {(input) => this.input = input}
					required={true} maxLength={MAX_DOMAIN_NAME} pattern="[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*" />
				</p>
			</div>
			</GenericModal>);
	}
	
	handleDomainName = (event) => {
		const value = event.target.value;
		this.setState({domainName: value});
	}
	
	handleSave = () => {
		const domainName = '@' + this.input.value,
			newExtension = {domain: domainName, status: 'PENDING'};
		this.props.onNewExtension(newExtension);
	}
}

// List of Email domains - main export class
class EmailAuth extends React.Component {
	static propTypes = {
		// portal top - used as context for modal dialogs
		banner: PropTypes.instanceOf(Element).isRequired,
		
		// right side of portal top - used as context for help drop-dropdown
		bannerMenuBar: PropTypes.instanceOf(Element).isRequired
	}
	state = {
		searchNode: null,
		domainStorage: {data: [], status: {}, actions: {}}
	}
	// grid definition
	columns = [
		{field: 'domain', label: 'Email Extension', type: DATA_TYPE.TEXT, formatter: (item, pat)=>{return highlightText(item.domain, pat); }},
		{field: 'status', label: 'Status', type: DATA_TYPE.TEXT, formatter: (item, pat)=>{return highlightText(item.status, pat); }},
		{field: 'actions', label: 'Actions', type: DATA_TYPE.ACTION_MENU, formatter: (item)=>{return this.actionsFormatter(item);}}
	]
	
	constructor(props) {
		super(props);
		this.domainService = new StorageService({target: 'ppadmin/domain', idProperty: 'domain',
			updateStorage: this.updateDomainStorage});
	}

	// storage setter passed to service
	updateDomainStorage = ({fn}) => {
		this.setState(({domainStorage}) => {
			return {domainStorage: fn(domainStorage)};
		});
	}
	// status setter helper
	changeStatus = (phase, phaseStatus) => {
		this.setState(({domainStorage})=>{
			const status = domainStorage.status;
			if(phaseStatus){
				status[phase] = phaseStatus;
			}else{
				delete status[phase];
			}
			return {domainStorage: {data: domainStorage.data, actions: domainStorage.actions, status: status}};
		});
	}
	// dropdown menu formatter
	actionsFormatter = (rowItem) => {
		if(impersonation){
			return 'Service Mode';
		}
		// Show approve link only if item is pending. Enable if item was created by different admin.
		let actionItems = [];
		if(rowItem.status === 'PENDING'){
			actionItems.push({id: 'approve', label: 'Approve Extension', icon: 'fa fa-check-square fa-lg', type: (rowItem.lid === FI.loginUser.lid ? '' : MENU_TYPE.LINK)});
		}
		actionItems.push({id: 'delete', label: 'Delete Extension', icon: 'fa fa-trash-o fa-lg', type: MENU_TYPE.LINK});
		const status = (this.state.domainStorage.actions || {})[rowItem.domain],
			triggerClass = status === STATUS.LOADING ? 'is-loading' : '',
			iconClass = 'fa ' + (status === STATUS.FAILED ? 'fa-exclamation-triangle' : 'fa-angle-down');
		return (
			<GenericDropdown items={actionItems} onItemClick={(actionItem)=>{this.handleMenuClick(actionItem, rowItem); }}
				iconClass={iconClass} triggerLabel="Action" triggerClass={triggerClass}/>
		);
	}
	
	// dropdown click dispatcher
	handleMenuClick = (actionItem, rowItem)=>{
		if(actionItem.id === 'delete'){
			this.deleteItem(rowItem);
		}else if(actionItem.id === 'approve'){
			this.approveItem(rowItem);
		}
	}
	
	// callback that opens new-domain dialog
	onAddButtonClick = () => {
		// disable button in impersonation mode
		if(!impersonation){
			this.changeStatus('add', STATUS.WAIT4INPUT);
		}
	}
	
	// cancelation callback from new-extension dialog
	onExtensionModalClose = () => {
		this.changeStatus('add');
	}
	
	// save callback from new-extension dialog
	onNewExtension = (newExtension) => {
		this.domainService.add(newExtension);
	}
	
	// delete callback from action dropdown
	deleteItem = (rowItem) => {
		this.domainService.del(rowItem);
	}
	
	// approve callback from action dropdown
	approveItem = (rowItem)=> {
		const payload = {domain: rowItem.domain, status: 'ENABLED'};
		this.domainService.patch(payload);
	}
	
	componentDidMount(){		
	 	this.domainService.load();
	 	this.setState({searchNode: this.refs.searchNode});
	}
	
	render(){
		const storage = this.state.domainStorage,
			addStatus = storage.status.add,
			hasModal = addStatus === STATUS.WAIT4INPUT,
			loadingAdd = addStatus === STATUS.LOADING,
			failedAdd = addStatus === STATUS.FAILED,
			newModal = hasModal ? <NewExtensionModal rootNode={this.props.banner}
				onClose={this.onExtensionModalClose} onNewExtension={this.onNewExtension} /> : null,
			addFailIcon = failedAdd ? <span className="icon"><i className="fa fa-exclamation-triangle"></i></span> : null;
		let addButtonText = 'Add New Extension';
			if(failedAdd){
				addButtonText = <span> {addButtonText}</span>;
			}
		return (
			<div>
				<div style={{position: 'absolute', top: 10, height: 50, right: 100, left: 100, textAlign: 'center'}}>
					<div style={{float: 'left'}}>
						<a className={'button is-outlined is-small is-info' + (loadingAdd ? ' is-loading' : '')} onClick={this.onAddButtonClick} disabled={impersonation}>
						{addFailIcon}
						{addButtonText}
						</a>
					</div>
					<div ref="searchNode" style={{float: 'right'}}></div>
				</div>
				<div style={{position: 'absolute', top: 50, bottom: 0, right: 100, left: 100}}>
					<GenericGrid columns={this.columns} hasHeader={true} hasInlineFilter={false}
						message={storage}
						search={{node: this.state.searchNode, placeholder: 'search here'}} />
					<HelpDropdown rootNode={this.props.bannerMenuBar} />		
				</div>
				{newModal}
			</div>
		);
	}
}

export default EmailAuth;