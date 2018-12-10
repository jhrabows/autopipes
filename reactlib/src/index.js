import React from 'react';
import ReactDOM from 'react-dom';

import loremIpsum from 'lorem-ipsum';
import Faker from 'faker';

//import GenericGrid from '../grid/GenericGrid.jsx';
//import GenericDropdown from '../dropdown/GenericDropdown.jsx';
//import HelpDropdown from '../dropdown/HelpDropdown.jsx';
//import {GenericModal, SimpleModal} from '../dialog/GenericModal.jsx';
import './Main.scss';

const columns = [
	{field: 'id', label: 'ID', type: 'N'},
	{field: 'flag', label: 'Flag', type: 'B', relativeWidth: 0.2},
	{field: 'name', label: 'Name', type: 'T'},
	{field: 'text', label: 'Text', type: 'T'},
	{field: 'changeDate', label: 'Change Date', type: 'D'}
	],
	items = [
		{id: 'menu1', label: 'Menu 1', type: 'link'},
		{id: 'divider1', type: 'divider'},
		{id: 'menu2', label: 'Menu 2'},
		{id: 'menu3', label: 'Menu 3'},
	],
	rowCount = 100,
	data = Array(rowCount).fill().map((val, idx) => {
	      return {
	        id: idx,
	        flag: (idx%2 === 0),
	        name: 'John Doe',
	        text: loremIpsum({
	          count: 1, 
	          units: 'sentences',
	          sentenceLowerBound: 4,
	          sentenceUpperBound: 8 
	        }),
	        changeDate: Faker.date.past()
	      }
	    }),
	ndGrid = document.getElementById('grid'),
	ndDropdown = document.getElementById('dropdown'),
	ndHelp = document.getElementById('help'),
	ndSimpleDialog = document.getElementById('simple-dialog'),
	ndDialog = document.getElementById('dialog');

const handleClick = (item) => {
  	alert(item.label + ' clicked');
},
handleClose = (name) => {
	alert('Clicked button:' + name);
},
handleScroll = (visibleItems) => {
	var visibleIds = visibleItems.map((item)=>{ return item.id; })
	console.log('visible item ids =' + JSON.stringify(visibleIds));
},
modalButtons =[
	{name: 'close', label: 'Close', handler: handleClose},
	{name: 'close', label: 'Disabled', handler: handleClose, disabled: true}
];


if(ndGrid){
	ReactDOM.render(<GenericGrid hasHeader={true} hasOverscan={false} onScroll={handleScroll} topRowCondition={function(item){ return item.id > 50; }} columns={columns} message={{status: 'sucess', data: data}} />, ndGrid);	
}else if(ndDropdown){
	ReactDOM.render(<GenericDropdown items={items} onItemClick={handleClick} />, ndDropdown);	
	if(ndHelp){
		var ndHelpRoot = document.getElementById('help-root');
		ReactDOM.render(<HelpDropdown rootNode={ndHelpRoot} rootTag='div' />, ndHelp);		
	}
}else if(ndDialog){
	var ndModalRoot = document.getElementById('modal-root');
	ReactDOM.render(<GenericModal buttons={modalButtons} rootNode={ndModalRoot} onClose={handleClose} title={"My Modal Title"} subTitle={"My Modal Sub Title"} >{"My Modal Content"}</GenericModal>, ndDialog);
}else if(ndSimpleDialog){
	var ndModalRoot = document.getElementById('modal-root');
	ReactDOM.render(<SimpleModal rootNode={ndModalRoot} onClose={handleClose} >{"Simple Modal Content"}</SimpleModal>, ndSimpleDialog);
}
