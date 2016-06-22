require("./node_modules/bootstrap/dist/css/bootstrap.min.css");
import React from 'react';
import ReactDOM from 'react-dom';
import {Grid} from './Ardagryd';
import data from './testData'
import ObjectEditor from './ObjectEditor'
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

export class EditorTest extends React.Component {
  constructor(props) {
    super(props);
    this.state = {object: {
      "name": "Vern Schuster Dr.",
      "username": "Delores.Kerluke62",
      "email": "Burnice_Kiehn87@yahoo.com",
      "male": true,
      "address": {
        "street": "edblvd",
        "suite": "Suite 381",
        "city": "Raynor land",
        "zipcode": "61031",
        "geo": {
          "lat": "-45.3177",
          "lng": "177.4623"
        }
      },
      "phone": "1-544-246-0502",
      "website": "lorena.name",
      "company": {
        "name": "Murray, Hirthe and Parisian",
        "catchPhrase": "Decentralized systemic productivity",
        "bs": "plug-and-play utilize experiences"
      },
      "id": 10
    }};

    this.updateObject = this.updateObject.bind(this);
  }

  updateObject(obj) {
    this.setState({object: obj});
  }


  render() {
    let config = [
      {
        propertyName: "name",
        label: "Name"
      },
      {propertyName: "address",

            config: [{propertyName: "geo", config:[{propertyName:"lat", label: "Latitude",
          changeHandler: (changedValue, parentHandler) => {
            console.log("Lat changed: "+ changedValue);
            parentHandler.call(this, "transformed lat "+ changedValue);
          } }]},
              {propertyName: "street", label:"Street", allowCustomValues: true, options: [{value:"edblvd", label:"Eddyson Blvd."},
                 {value:"edblvd2", label:"Eddyson Blvd. 2"}] }]
      }
    ];
    return (

      <div>
        <ObjectEditor id={"test-editor"} properties={config} object={this.state.object} changeHandler={this.updateObject}/>
      </div>
    );
  }
}

//ReactDOM.render(<App/>, document.querySelector("#myApp"));
ReactDOM.render(<EditorTest/>, document.querySelector("#myApp"));
