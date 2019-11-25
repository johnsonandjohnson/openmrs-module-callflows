import _ from 'lodash';
import axiosInstance from '../config/axios';
import { SUCCESS, REQUEST, FAILURE } from './action-type.util';
import ConfigFormData from '../components/config-form/config-form-data';
import { IFlow, defaultValue as defaultFlowValue } from '../shared/model/flow.model';
import { handleTestCallRequest, handleSuccessMessage } from '../components/designer/test-call/designer-call-test.util';
import * as Msg from '../shared/utils/messages';
import { handleRequest } from '../shared/utils/request-status-util';
import SystemMessage from '../shared/model/system-message.model';
import UserMessage from '../shared/model/user-message.model';
import { IFlowTestResponse } from '../shared/model/flow-test-response.model';
import { IFlowTestResponseBody } from '../shared/model/flow-test-response-body.model';
import { IContinueFieldProps } from '../shared/model/continue-field-props.model';
import { convertToType } from '../shared/utils/conversion-util';
import { AxiosPromise, AxiosResponse } from 'axios';
import { NodeUI, toModel, getNewUserNode, getNewSystemNode, toUI } from '../shared/model/node-ui';

export const ACTION_TYPES = {
  RESET: 'designerReducer/RESET',
  FETCH_CONFIGS: 'designerReducer/FETCH_CONFIGS',
  FETCH_FLOWS: 'designerReducer/FETCH_FLOWS',
  FETCH_FLOW: 'designerReducer/FETCH_FLOW',
  MAKE_TEST_CALL: 'designerReducer/MAKE_TEST_CALL',
  UPDATE_NODE: 'designerReducer/UPDATE_NODE',
  UPDATE_FLOW: 'designerReducer/UPDATE_FLOW',
  PUT_FLOW: 'designerReducer/PUT_FLOW',
  POST_FLOW: 'designerReducer/POST_FLOW',
  SEND_MESSAGE: 'designerReducer/SEND_MESSAGE',
  RESET_MESSAGES: 'designerReducer/RESET_MESSAGES',
  NODE_PROCESSED: 'designerReducer/NODE_PROCESSED',
  DELETE_INTERACTION_NODE: 'designerReducer/DELETE_INTERACTION_NODE',
  ADD_EMPTY_USER_AND_SYSTEM_NODES: 'designerReducer/ADD_EMPTY_USER_AND_SYSTEM_NODES'
};

const initialState = {
  configForms: [] as ReadonlyArray<any>,
  showModal: false,
  toDeleteId: null,
  pages: 0,
  loading: false,
  data: [],
  flow: defaultFlowValue as unknown as IFlow,
  flowLoaded: false,
  nodes: [] as Array<NodeUI>,
  messages: [] as ReadonlyArray<UserMessage | SystemMessage>,
  continueFieldProps: null as unknown as IContinueFieldProps
};

export type DesignerState = Readonly<typeof initialState>;

export default (state: DesignerState = initialState, action): DesignerState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.PUT_FLOW):
      return {
        ...state,
        loading: true
      };
    case FAILURE(ACTION_TYPES.PUT_FLOW):
      return {
        ...state,
        loading: false
      };
    case SUCCESS(ACTION_TYPES.PUT_FLOW):
      const flow = action.payload.data;
      const nodes = extractNodes(flow);
      return {
        ...state,
        loading: false,
        flow,
        nodes
      };
    case ACTION_TYPES.UPDATE_FLOW:
      return {
        ...state,
        flow: action.payload,
        nodes: extractNodes(action.payload)
      }
    case REQUEST(ACTION_TYPES.POST_FLOW):
      return {
        ...state,
        loading: true
      };
    case FAILURE(ACTION_TYPES.POST_FLOW):
      return {
        ...state,
        loading: false
      };
    case SUCCESS(ACTION_TYPES.POST_FLOW):
      return {
        ...state,
        loading: false,
        flow: action.payload.data,
        nodes: extractNodes(action.payload.data)
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
        ...state,
        flowLoaded: false
      };
    case FAILURE(ACTION_TYPES.FETCH_FLOW):
      return {
        ...state,
        flowLoaded: false
      };
    case SUCCESS(ACTION_TYPES.FETCH_FLOW): {
      const flow = action.payload.data.results[0] as IFlow;
      let nodes = extractNodes(flow);
      return {
        ...state,
        flow,
        nodes,
        flowLoaded: true
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
    case ACTION_TYPES.NODE_PROCESSED: {
      return {
        ...state,
        messages: [...state.messages, ...action.payload],
        continueFieldProps: action.meta
      };
    }
    case ACTION_TYPES.ADD_EMPTY_USER_AND_SYSTEM_NODES: {
      return {
        ...state,
        nodes: addNewUserAndSystemNodes(state.nodes)
      }
    }
    case ACTION_TYPES.UPDATE_NODE: {
      return {
        ...state,
        nodes: replaceNode(state.nodes, action.payload, action.meta)
      }
    }
    case ACTION_TYPES.SEND_MESSAGE: {
      return {
        ...state,
        messages: [...state.messages, action.payload]
      }
    }
    case ACTION_TYPES.RESET_MESSAGES: {
      return {
        ...state,
        messages: []
      };
    }
    case ACTION_TYPES.RESET: {
      return initialState;
    }
    case ACTION_TYPES.DELETE_INTERACTION_NODE: {
      return {
        ...state,
        nodes: removeInteractioNode(state.nodes, action.payload)
      }
    }
    default:
      return state;
  }
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});

export const resetMessages = () => ({
  type: ACTION_TYPES.RESET_MESSAGES
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
  dispatch({
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

export const putFlow = (flow: IFlow, nodes: Array<NodeUI>) => async (dispatch) => {
  const requestUrl = `${callflowsPath}/flows/${flow.id}`;
  const data = {
    ...flow,
    raw: JSON.stringify({
      nodes: _.map(nodes, toModel),
      name: flow.name
    })
  };
  delete data.id;
  let body = {
    type: ACTION_TYPES.PUT_FLOW,
    payload: axiosInstance.put(requestUrl, data)
  };
  handleRequest(dispatch, body, Msg.DESIGNER_FLOW_UPDATE_SUCCESS, Msg.DESIGNER_FLOW_UPDATE_FAILURE);
};

export const addEmptyInteractionNode = () => ({
  type: ACTION_TYPES.ADD_EMPTY_USER_AND_SYSTEM_NODES,
});

export const updateNode = (node: NodeUI, nodeIndex: number) => ({
  type: ACTION_TYPES.UPDATE_NODE,
  payload: node,
  meta: nodeIndex
});

export const deleteInteractionNode = (nodeIndex: number) => ({
  type: ACTION_TYPES.DELETE_INTERACTION_NODE,
  payload: nodeIndex
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

export const sendMessage = (message: UserMessage) => async (dispatch, getState) => {
  const state: DesignerState = getState().designerReducer;
  const { continueFieldProps } = state;
  const { callId } = continueFieldProps;
  const params = {};
  params[continueFieldProps.name] = message.text;
  await dispatch({
    type: ACTION_TYPES.NODE_PROCESSED,
    payload: [message],
    meta: continueFieldProps
  });
  moveToNextNode(callId, dispatch, params);
};

export const initiateTestFlow = (configName: string, flowName: string) => async (dispatch) => {
  // on the backend any configuration is required
  const requestUrl = `${callflowsPath}/in/${configName}/flows/${flowName}.json`;
  const { data } = await axiosInstance.get(requestUrl);
  processNodeResponse(data, dispatch);
};

export const processNodeResponse = (data: IFlowTestResponse, dispatch: Function) => {
  const response = data;
  if (!isJsonContent(response.body)) {
    dispatch({
      type: ACTION_TYPES.NODE_PROCESSED,
      payload: [new SystemMessage(response.node, response.body)],
      meta: null
    });
    return;
  }
  const responseContent: [IFlowTestResponseBody] = JSON.parse(response.body);
  const newMessages = responseContent.map(m => new SystemMessage(response.node, m.txt));
  let continueFieldProps = extractContinueFieldProps(responseContent, response.callId);
  dispatch({
    type: ACTION_TYPES.NODE_PROCESSED,
    payload: newMessages,
    meta: continueFieldProps
  });

  if (response.continueNode) {
    moveToNextNode(response.callId, dispatch);
  }
};

export const moveToNextNode = async (callId: string, dispatch: Function, params: any = {}) => {
  const requestUrl = `${callflowsPath}/calls/${callId}.json`;
  try {
    let response = await axiosInstance.get(requestUrl, {
      params: {
        ...params
      }
    });
    processNodeResponse(response.data, dispatch);
  } catch (e) {
    handleNodeError(e, dispatch);
  }
};

const extractContinueFieldProps = (responseContent: IFlowTestResponseBody[], callId: string) => {
  if (responseContent.length > 0) {
    const lastMessage = responseContent[responseContent.length - 1];
    if (lastMessage.field) {
      return {
        name: lastMessage.field,
        type: lastMessage.type,
        callId
      };
    }
  } else return null;
};

export const updateFlow = (payload: any) => ({
  type: ACTION_TYPES.UPDATE_FLOW,
  payload
});

export const postFlow = (flow: IFlow, nodes: Array<NodeUI>) => async (dispatch) => {
  const requestUrl = `${callflowsPath}/flows`;
  const data = {
    ...flow,
    raw: JSON.stringify({
      nodes: _.map(nodes, toModel),
      name: flow.name
    })
  };
  delete data.id;
  let body = {
    type: ACTION_TYPES.POST_FLOW,
    payload: axiosInstance.post(requestUrl, data)
  };
  handleRequest(dispatch,
    body,
    Msg.DESIGNER_FLOW_CREATE_SUCCESS,
    Msg.DESIGNER_FLOW_CREATE_FAILURE);
};

const addNewUserAndSystemNodes = (nodes: Array<NodeUI>): Array<NodeUI> => {
  return [
    ...nodes,
    getNewUserNode(),
    getNewSystemNode()
  ]
}

const replaceNode = (nodes: Array<NodeUI>, node: NodeUI, nodeIndex: number): Array<NodeUI> => {
  return nodes.map((item, index: number) => {
    if (index === nodeIndex) {
      item = node;
    }
    return item;
  });
};

const removeInteractioNode = (nodes: Array<any>, userNodeIndex: number) => {
  // the one interaction node is consists of by the User and the System nodes
  // that's why we add 1 to calculate index of system node
  const systemNodeIndex = userNodeIndex + 1;
  return nodes.filter((item, index: number) => {
    if (index !== userNodeIndex && index !== systemNodeIndex) {
      return item;
    }
  });
};

const extractNodes = (flow: IFlow) => {
  let modals = [];
  try {
    modals = JSON.parse(flow.raw).nodes || [];
  } catch (ex) {
    console.error('Cannot parse nodes');
  }
  return _.map(modals, toUI);
}

const handleNodeError = (e, dispatch) => {
  let errorMessage = 'Error';
  try {
    try {
      try {
        errorMessage = e.response.data.message;
        if (!errorMessage) throw 'Blank error';
      } catch (ex) {
        errorMessage = JSON.stringify(e.response.data);
      }
    } catch (exc) {
      errorMessage = JSON.stringify(e);
    }
  } catch (exception) {
    errorMessage = exception;
  }

  dispatch({
    type: ACTION_TYPES.NODE_PROCESSED,
    payload: [new SystemMessage('error', errorMessage, new Date(), 'red')],
    meta: null
  });
};

const isJsonContent = (text: string) => {
  try {
    const parsed = JSON.parse(text);
    return !!parsed;
  } catch (e) {
    return false;
  }
};
