import axiosInstance from '../config/axios';
import { SUCCESS, REQUEST, FAILURE } from './action-type.util';
import ConfigFormData from '../components/config-form/config-form-data';
import { IFlow, defaultValue } from '../shared/model/flow.model';
import { INode } from '../shared/model/node.model';
import { handleTestCallRequest, handleSuccessMessage } from '../components/designer/designer-flow-test.util';
import * as Msg from '../shared/utils/messages';
import { handleRequest } from '../shared/utils/request-status-util';

export const ACTION_TYPES = {
  RESET: 'designerReducer/RESET',
  FETCH_CONFIGS: 'designerReducer/FETCH_CONFIGS',
  FETCH_FLOWS: 'designerReducer/FETCH_FLOWS',
  FETCH_FLOW: 'designerReducer/FETCH_FLOW',
  MAKE_TEST_CALL: 'designerReducer/MAKE_TEST_CALL',
  UPDATE_NODE: 'designerReducer/UPDATE_NODE',
  UPDATE_FLOW: 'designerReducer/UPDATE_FLOW'
};

const initialState = {
  configForms: [] as ReadonlyArray<any>,
  showModal: false,
  toDeleteId: null,
  pages: 0,
  loading: false,
  data: [],
  flow: defaultValue as unknown as IFlow,
  nodes: [] as Array<INode>
};

export type DesignerState = Readonly<typeof initialState>;

export default (state: DesignerState = initialState, action): DesignerState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.UPDATE_FLOW):
      return {
        ...state,
        loading: true
      };
    case FAILURE(ACTION_TYPES.UPDATE_FLOW):
      return {
        ...state,
        loading: false
      };
    case SUCCESS(ACTION_TYPES.UPDATE_FLOW):
      let flow = action.payload.data;
      let nodes = extractNodes(flow);
      return {
        ...state,
        flow,
        nodes
      };
    case REQUEST(ACTION_TYPES.FETCH_CONFIGS):
      return {
        ...state
      };
    case FAILURE(ACTION_TYPES.FETCH_CONFIGS):
      return {
        ...state
      };
    case SUCCESS(ACTION_TYPES.FETCH_CONFIGS):
      return {
        ...state,
        configForms: action.payload.data.map((fetched) => {
          return new ConfigFormData(fetched);
        })
      };
    case REQUEST(ACTION_TYPES.FETCH_FLOWS):
      return {
        ...state
      };
    case FAILURE(ACTION_TYPES.FETCH_FLOWS):
      return {
        ...state
      };
    case SUCCESS(ACTION_TYPES.FETCH_FLOWS):
      return {
        ...state,
        data: action.payload.data.results
      }
    case REQUEST(ACTION_TYPES.FETCH_FLOW):
      return {
        ...state
      };
    case FAILURE(ACTION_TYPES.FETCH_FLOW):
      return {
        ...state
      };
    case SUCCESS(ACTION_TYPES.FETCH_FLOW): {
      const flow = action.payload.data.results[0] as IFlow;
      let nodes = extractNodes(flow);
      return {
        ...state,
        flow,
        nodes
      }
    }
    case REQUEST(ACTION_TYPES.MAKE_TEST_CALL):
      return {
        ...state
      };
    case FAILURE(ACTION_TYPES.MAKE_TEST_CALL):
      return {
        ...state
      };
    case SUCCESS(ACTION_TYPES.MAKE_TEST_CALL):
      handleSuccessMessage(action.payload.data, action.meta);
      return {
        ...state,
      }
    case ACTION_TYPES.UPDATE_NODE: {
      return {
        ...state,
        nodes: replaceNode(state.nodes, action.payload, action.meta)
      }
    }
    case ACTION_TYPES.RESET: {
      return initialState;
    }
    default:
      return state;
  }
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});

const callflowsPath = 'ws/callflows';

export const postConfigs = (configForms) => async (dispatch) => {
  const requestUrl = callflowsPath + '/configs';
  let data = configForms.map((form) => {
    return form.config.getModel();
  });
  await dispatch({
    type: ACTION_TYPES.FETCH_CONFIGS,
    payload: axiosInstance.post(requestUrl, data)
  });
};

export const getConfigs = () => async (dispatch) => {
  const requestUrl = callflowsPath + '/configs';
  await dispatch({
    type: ACTION_TYPES.FETCH_CONFIGS,
    payload: axiosInstance.get(requestUrl)
  });
};

export const getFlows = (filters: any = {}) => async (dispatch) => {
  const term = filters.flowName ? filters.flowName : '';
  const requestUrl = `${callflowsPath}/flows?lookup=By+Name&term=${term}`;
  await dispatch({
    type: ACTION_TYPES.FETCH_FLOWS,
    payload: axiosInstance.get(requestUrl)
  });
};

export const getFlow = (flowName: string) => async (dispatch) => {
  //currently there is no endpoint for fetching one instance
  const requestUrl = `${callflowsPath}/flows?lookup=By+Name&term=${flowName}`;
  await dispatch({
    type: ACTION_TYPES.FETCH_FLOW,
    payload: axiosInstance.get(requestUrl)
  });
};

export const updateFlow = (flow: IFlow, nodes: Array<INode>) => async (dispatch) => {
  const requestUrl = `${callflowsPath}/flows/${flow.id}`;
  const data = {
    ...flow,
    raw: JSON.stringify({ nodes: nodes })
  }
  delete data.id;
  let body = {
    type: ACTION_TYPES.UPDATE_FLOW,
    payload: axiosInstance.put(requestUrl, data)
  };
  handleRequest(dispatch, body, Msg.DESIGNER_FLOW_UPDATE_SUCCESS, Msg.DESIGNER_FLOW_UPDATE_FAILURE);
};

export const updateNode = (node: any, nodeIndex: number) => ({
  type: ACTION_TYPES.UPDATE_NODE,
  payload: node,
  meta: nodeIndex
});

export const makeTestCall = (config: string, flow: string, phone: string, extension: string) => async (dispatch) => {
  const requestUrl = `${callflowsPath}/out/${config}/flows/${flow}.${extension}`
  const body = {
    type: ACTION_TYPES.MAKE_TEST_CALL,
    payload: axiosInstance.get(requestUrl, {
      params: {
        phone
      }
    })
  };
  handleTestCallRequest(dispatch, body);
}

const replaceNode = (nodes: Array<any>, node: any, nodeIndex: number) => {
  return nodes.map((item, index: number) => {
    if (index === nodeIndex) {
      item = node;
    }
    return item;
  });
}

const extractNodes = (flow: IFlow) => {
  let nodes = [];
  try {
    nodes = JSON.parse(flow.raw).nodes;
  } catch (ex) {
    console.error('Cannot parse nodes');
  }
  return nodes;
}
