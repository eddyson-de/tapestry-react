require("./node_modules/bootstrap/dist/css/bootstrap.min.css");
import React from 'react';
import ReactDOM from 'react-dom';
import {Grid} from './Ardagryd';
import data from './testData'
import 'react-select/dist/react-select.css';


export class App extends React.Component {



	render() {

    let externalData = {getThis: "External data"};


    var config = {showToolbar: true, paging: 10};
    var columns = {
      name: {
        sort: true,
        displayValueGetter: ({value, object, columns}) => <span>{value}</span>
      },
      edit: {
        label: "Edit",
        hideTools: true,
        displayValueGetter: ({value, object, columns}) => <a href={"#"}> EDIT ROW</a>
      },
      id: {show: false}
    };

		return (

      <div>
        <Grid objects={data} columns={columns} config={config} />
      </div>
		);
	}
}


ReactDOM.render(<App/>, document.querySelector("#myApp"));
