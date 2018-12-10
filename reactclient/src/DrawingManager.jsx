import React from 'react';
import StorageService from 'shared-service/JsonStore.js';

import './App.scss';

class DrawingManager extends React.Component {
    state = {
		drawingStorage: {data: [], status: {}, actions: {}},
        areaStorage: {data: [], status: {}, actions: {}}
    }

// lifecycle
	constructor(props) {
		super(props);
		this.drawingService = new StorageService({target: 'drawing', idProperty: 'id',
            updateStorage: this.updateDrawingStorage});
        this.areaService = new StorageService({target: ['drawing', 'area'],
            idProperty: ['drawingId', 'areaId'],
            updateStorage: this.updateAreaStorage});
        this.drawingSelectionRef = React.createRef();
        this.areaSelectionRef = React.createRef();
        this.sheetTypeSelectionRef = React.createRef();
    }
    componentDidMount(){		
        this.drawingService.load();
    }
    render(){
        return(
        <div>
            <select onChange={this.handleDrawingSelect} ref={this.drawingSelectionRef}>
                <option value="">Select drawing</option>{
                this.state.drawingStorage.data.map(({id, dwgName})=>
                    <option key={id} value={id}>{dwgName}</option>
                )
            }</select>
            <select ref={this.areaSelectionRef}>
                <option value="">Select area</option>{
                this.state.areaStorage.data.map(({areaId, areaName})=>
                    <option key={areaId} value={areaId}>{areaName}</option>
                )
            }</select>
            <select ref={this.sheetTypeSelectionRef}>{
                Autopipes.csTypes.map(({id, name})=>
                    <option key={id} value={id}>{name}</option>
                )
            }</select>
            <div>
            <button onClick={this.handleAreaDeleta}>Delete Area</button>
            <button onClick={this.handleCutSheet}>Cut Sheet</button>
            </div>

        </div>
        );
    }

    // custom
    handleCutSheet = () => {
        const dwgId = this.drawingSelectionRef.current.value,
            areaId = this.areaSelectionRef.current.value,
            sheetTypeSplit = this.sheetTypeSelectionRef.current.value.split('_'),
            sheetType = sheetTypeSplit[0],
            rtl = sheetTypeSplit[1] || 'N';
    }
    handleAreaDeleta = () => {
        const dwgId = this.drawingSelectionRef.current.value,
            areaId = this.areaSelectionRef.current.value;
            this.areaService.del({path: [dwgId, areaId]});
    }

    handleDrawingSelect = ({target}) => {
        const dwgId =  target.value;
        this.areaService.load({path: [dwgId]});
    }
	// storage setters passed to services
	updateDrawingStorage = ({fn}) => {
		this.setState(({drawingStorage}) => {
			return {drawingStorage: fn(drawingStorage)};
		});
	}
	updateAreaStorage = ({fn}) => {
		this.setState(({areaStorage}) => {
			return {areaStorage: fn(areaStorage)};
		});
	}

}

export default DrawingManager;
