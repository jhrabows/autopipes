import React from 'react';
import ReactDOM from 'react-dom';
import DwgMgr from './DrawingManager.jsx';

window.initLayout = function(){
	const contentNode = document.querySelector('[data-layout-name]'),
		layoutName = contentNode.getAttribute('data-layout-name');
	if(layoutName === 'dwgMgr'){
		ReactDOM.render(<DwgMgr />, contentNode);
	}
};

window.initLayout();

