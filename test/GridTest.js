import React          from 'react';
import TestUtils      from 'react-addons-test-utils';
import console        from 'console';
import { Grid } from '../Ardagryd';
import ReactDOM from 'react-dom'
import should from 'should'

let Simulate = TestUtils.Simulate;

let data = [{
  "name": "Nike Floder",
  "username": "Katrin_Bussmann",
  "email": "Emilian20@yahoo.com",
  "address": {
    "street": "Krodinger Islands",
    "suite": "Suite 232",
    "city": "Dittmer land",
    "zipcode": "28357",
    "geo": {
      "lat": "79.8318",
      "lng": "160.5794"
    }
  },
  "phone": "(05877) 2173075",
  "website": "gregor.info",
  "company": {
    "name": "Krebs - Salzmann",
    "catchPhrase": "Seamless impactful toolset",
    "bs": "enterprise exploit relationships"
  }
}, {
  "name": "Sydnie Heller",
  "username": "Gennaro23",
  "email": "Wilton.Goldner@hotmail.com",
  "address": {
    "street": "Hauck Keys",
    "suite": "Apt. 632",
    "city": "Deondre town",
    "zipcode": "85521",
    "geo": {
      "lat": "-18.2952",
      "lng": "-105.5756"
    }
  },
  "phone": "1-718-924-6202",
  "website": "telly.biz",
  "company": {
    "name": "Russel - Murazik",
    "catchPhrase": "Focused system-worthy firmware",
    "bs": "leading-edge expedite interfaces"
  }
}, {
  "name": "Mrs. Ezekiel Mraz",
  "username": "Elenora46",
  "email": "Karl36@hotmail.com",
  "address": {
    "street": "Allene Heights",
    "suite": "Apt. 368",
    "city": "Birdie burgh",
    "zipcode": "37617",
    "geo": {
      "lat": "24.5139",
      "lng": "51.1659"
    }
  },
  "phone": "771.904.8274 x95796",
  "website": "myles.info",
  "company": {
    "name": "Prohaska, Kerluke and Weimann",
    "catchPhrase": "Cloned mission-critical function",
    "bs": "holistic scale deliverables"
  }
}, {
  "name": "Benedict Brakus",
  "username": "May18",
  "email": "Willie_Hahn58@hotmail.com",
  "address": {
    "street": "Sammie Summit",
    "suite": "Suite 722",
    "city": "Jamie berg",
    "zipcode": "31277",
    "geo": {
      "lat": "-43.6279",
      "lng": "44.3682"
    }
  },
  "phone": "(523) 621-8921 x83064",
  "website": "alayna.org",
  "company": {
    "name": "Schuster and Sons",
    "catchPhrase": "Enterprise-wide analyzing middleware",
    "bs": "ubiquitous iterate methodologies"
  }
}, {
  "name": "Tyrel Feest",
  "username": "Lulu63",
  "email": "Bonita_Nolan8@yahoo.com",
  "address": {
    "street": "Norene Glens",
    "suite": "Apt. 910",
    "city": "South Evangeline",
    "zipcode": "47574-8277",
    "geo": {
      "lat": "-7.7786",
      "lng": "58.9901"
    }
  },
  "phone": "(884) 180-0236",
  "website": "bernadine.name",
  "company": {
    "name": "Welch Inc",
    "catchPhrase": "Switchable incremental productivity",
    "bs": "interactive disintermediate methodologies"
  }
}, {
  "name": "Ms. Kathlyn Schumm",
  "username": "Fiona.Monahan65",
  "email": "Gonzalo_Nikolaus@yahoo.com",
  "address": {
    "street": "Daugherty Prairie",
    "suite": "Suite 833",
    "city": "Eichmann mouth",
    "zipcode": "39796",
    "geo": {
      "lat": "89.3292",
      "lng": "43.1382"
    }
  },
  "phone": "1-774-284-3018",
  "website": "margarita.net",
  "company": {
    "name": "Howe and Sons",
    "catchPhrase": "Team-oriented zero administration time-frame",
    "bs": "holistic extend bandwidth"
  }
}, {
  "name": "Jayne Volkman",
  "username": "Sallie94",
  "email": "Caleb_Gerhold60@gmail.com",
  "address": {
    "street": "MacGyver Pines",
    "suite": "Suite 079",
    "city": "Austen mouth",
    "zipcode": "12175-5795",
    "geo": {
      "lat": "-54.8044",
      "lng": "4.8077"
    }
  },
  "phone": "370-337-6019 x73126",
  "website": "larissa.com",
  "company": {
    "name": "Zemlak - Tremblay",
    "catchPhrase": "Monitored object-oriented throughput",
    "bs": "leading-edge disintermediate models"
  }
}, {
  "name": "Christopher Haag",
  "username": "Jedidiah18",
  "email": "Omari_Sporer@yahoo.com",
  "address": {
    "street": "Leannon Plaza",
    "suite": "Suite 851",
    "city": "West Scot",
    "zipcode": "81043-7591",
    "geo": {
      "lat": "-57.6500",
      "lng": "116.2686"
    }
  },
  "phone": "(204) 045-9662 x454",
  "website": "caleb.com",
  "company": {
    "name": "Wolff and Sons",
    "catchPhrase": "Diverse explicit frame",
    "bs": "revolutionary optimize users"
  }
}];

/**
 * Renders a component inside of a container
 *
 * @param {React.Component} component The component to test
 * @param {Object} componentProps={}  The default props to set on the component
 * @return {Array} The container component as the first item, the component to test as the second item
 */
function renderInContainer(component, componentProps={}) {

  class PropChangeContainer extends React.Component {

    constructor(props) {

      super(props);

      // set the state of the container from it's props (which will be the default
      // componentProps) passed to the function
      this.state = props;

    }

    render() {

      // render the component within the container and pass the container state
      // as the component's initial props
      return React.createElement(component, this.state);

    }

  }

  // get both the container and component instances and return them
  let container = TestUtils.renderIntoDocument(<PropChangeContainer {...componentProps} />);
  let instance = TestUtils.findRenderedComponentWithType(container, component);

  return [
    container,
    instance
  ];

}



describe('Grid render tests', function(){

  it('Should render Grid with 8 rows', function(){
    let grid = TestUtils.renderIntoDocument(
      <Grid objects={data} columns={{
      name: {
        sort: true,
        displayValueGetter: ({value, object, columns}) => <span>{value}</span>
      }}} config={{}}/>
    );
    let tbody = TestUtils.scryRenderedDOMComponentsWithTag(grid, "tbody")[0];
    let tbodyDOM = ReactDOM.findDOMNode(tbody);

    should(tbodyDOM.childNodes.length).be.exactly(8);
  });

  it('Should filter grid to 2 rows', function (){

    let grid = TestUtils.renderIntoDocument(
      <Grid objects={data} columns={{
      name: {
        sort: true,
        filter: "ie"
      }}} config={{}}/>
    );

    let tbody = TestUtils.scryRenderedDOMComponentsWithTag(grid, "tbody")[0];
    let tbodyDOM = ReactDOM.findDOMNode(tbody);

    should(tbodyDOM.childNodes.length).be.exactly(2);
  });

  it('Should shout sort by name in descending order', function (){

    let grid = TestUtils.renderIntoDocument(
      <Grid objects={data} columns={{
      name: {
        sort: "desc"
      }}} config={{}}/>
    );

    let tbody = TestUtils.scryRenderedDOMComponentsWithTag(grid, "tbody")[0];
    let tbodyDOM = ReactDOM.findDOMNode(tbody);

    should(tbodyDOM.childNodes[0].childNodes[0].textContent).be.exactly("Tyrel Feest");
  });

  it('Should have 8 columns by default', function (){

    let grid = TestUtils.renderIntoDocument(
      <Grid objects={data} columns={{
      name: {
      }}} config={{}}/>
    );

    let tbody = TestUtils.scryRenderedDOMComponentsWithTag(grid, "tbody")[0];
    let tbodyDOM = ReactDOM.findDOMNode(tbody);

    should(tbodyDOM.childNodes[0].childNodes.length).be.exactly(8);
  });

  it('Should hide 2 columns', function (){

    let grid = TestUtils.renderIntoDocument(
      <Grid objects={data} columns={{
      name: {
        show: false
      },
      id:{show: false}}} config={{}}/>
    );

    let tbody = TestUtils.scryRenderedDOMComponentsWithTag(grid, "tbody")[0];
    let tbodyDOM = ReactDOM.findDOMNode(tbody);

    should(tbodyDOM.childNodes[0].childNodes.length).be.exactly(6);
  });

  it('Should react to changing properties', function (){

    let [container, instance] = renderInContainer(Grid, { objects: [], columns: {}, config: {showColumnsWithoutConfig: false} });

    let tbody = TestUtils.scryRenderedDOMComponentsWithTag(instance, "tbody")[0];
    let tbodyDOM = ReactDOM.findDOMNode(tbody);

    should(tbodyDOM.childNodes.length).be.exactly(0);

    container.setState({objects:data, columns:{name: { show: true }}});
    should(tbodyDOM.childNodes.length).be.exactly(8);
    should(tbodyDOM.childNodes[0].childNodes.length).be.exactly(1);

  });
  
  it('Should be possible to override the cell renderer per column', function (){

    let grid = TestUtils.renderIntoDocument(
      <Grid objects={data} columns={{
        name: {
          order: 0,
          cellRendererBase: ({object: {name, email}})=><a href={`mailto:${email}`}>{name}</a>
        }
        }} config={{}}/>
    );

    let tbody = TestUtils.scryRenderedDOMComponentsWithTag(grid, "tbody")[0];
    let tbodyDOM = ReactDOM.findDOMNode(tbody);

    should(tbodyDOM.childNodes[0].childNodes[0].innerHTML).be.exactly('<a href="mailto:Emilian20@yahoo.com">Nike Floder</a>');
  });

  it('Should be possible to override the displayValueGetter per column', function (){

    let grid = TestUtils.renderIntoDocument(
      <Grid objects={data} columns={{
        name: {
          order: 0,
          displayValueGetter: ({object})=><span>John Doe</span>
        }
        }} config={{}}/>
    );

    let tbody = TestUtils.scryRenderedDOMComponentsWithTag(grid, "tbody")[0];
    let tbodyDOM = ReactDOM.findDOMNode(tbody);

    should(tbodyDOM.childNodes[0].childNodes[0].innerHTML).be.exactly("<span>John Doe</span>");
  });
  
  it('Should be possible to override global displayValueGetter', function (){

    let grid = TestUtils.renderIntoDocument(
      <Grid objects={data} columns={{
        name: {
          order: 0
        }
        }} config={{
          displayValueGetter: ({object})=><span>This is the name</span>
        }}/>
    );

    let tbody = TestUtils.scryRenderedDOMComponentsWithTag(grid, "tbody")[0];
    let tbodyDOM = ReactDOM.findDOMNode(tbody);

    should(tbodyDOM.childNodes[0].childNodes[0].innerHTML).be.exactly("<span>This is the name</span>");
  });
  
  it('Should be possible to override the global displayValueGetter with a per-column configuration', function (){

    let grid = TestUtils.renderIntoDocument(
      <Grid objects={data} columns={{
        name: {
          order: 0,
          displayValueGetter: ({object})=><span>Robert Paulson</span>
        }
        }} config={{
          displayValueGetter: ({object})=><span>This is the name</span>
        }}/>
    );

    let tbody = TestUtils.scryRenderedDOMComponentsWithTag(grid, "tbody")[0];
    let tbodyDOM = ReactDOM.findDOMNode(tbody);

    should(tbodyDOM.childNodes[0].childNodes[0].innerHTML).be.exactly("<span>Robert Paulson</span>");
  });
  
  it('Should render an array value', function (){

    let grid = TestUtils.renderIntoDocument(
      <Grid objects={[{nickNames: ["Dude", "Johnny"]}]} columns={{
        nickNames: {
          order: 0}
        }} config={{}}/>
    );

    let tbody = TestUtils.scryRenderedDOMComponentsWithTag(grid, "tbody")[0];
    let tbodyDOM = ReactDOM.findDOMNode(tbody);

    should(tbodyDOM.childNodes[0].childNodes[0].innerHTML).be.exactly("<ul><li><span>Dude</span></li><li><span>Johnny</span></li></ul>");
  });

  
  it('Can use a component class as displayValueGetter', function (){
    
    class Renderer extends React.Component {
      
      constructor(props){
        super(props);
      }
      
      render(){
        return <span className="custom">{this.props.value}</span>;
      }
      
    }

    let grid = TestUtils.renderIntoDocument(
      <Grid objects={[{name: "John Doe"}]} columns={{
        name: {
          order: 0,
          displayValueGetter: Renderer
        }
        }} config={{}}/>
    );

    let tbody = TestUtils.scryRenderedDOMComponentsWithTag(grid, "tbody")[0];
    let tbodyDOM = ReactDOM.findDOMNode(tbody);

    should(tbodyDOM.childNodes[0].childNodes[0].innerHTML).be.exactly('<span class="custom">John Doe</span>');
  });

});