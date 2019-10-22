import axiosInstance from '../config/axios';
import { SUCCESS, REQUEST, FAILURE } from './action-type.util';

export const ACTION_TYPES = {
    RESET: 'renderersReducer/RESET',
    FETCH_RENDERERS: 'renderersReducer/FETCH_RENDERERS',
    CREATE_RENDERER: 'renderersReducer/CREATE_RENDERER'
  };

  const initialState = {
      renderers: []
  };

export default (state = initialState, action) => {
    switch (action.type) {
        case REQUEST(ACTION_TYPES.CREATE_RENDERER):
            return {
                ...state
            };
        case FAILURE(ACTION_TYPES.CREATE_RENDERER):
            return {
                ...state
            };
        case SUCCESS(ACTION_TYPES.CREATE_RENDERER):
            return {
                ...state,
                renderers: action.payload.data
            };
        case REQUEST(ACTION_TYPES.FETCH_RENDERERS):
            return {
                ...state
            };
        case FAILURE(ACTION_TYPES.FETCH_RENDERERS):
            return {
                ...state
            };
        case SUCCESS(ACTION_TYPES.FETCH_RENDERERS):
            return {
                ...state,
                renderers: action.payload.data
            };
        case ACTION_TYPES.RESET: {
            return {
                ...state,
                renderers: []
            };
        }
        default:
            return state;
    }
};


const callflowsPath = 'ws/callflows';

export const reset = () => ({
    type: ACTION_TYPES.RESET 
});

export const getRenderers = () => async (dispatch) => {
    const requestUrl = callflowsPath + '/renderers';
    await dispatch({
        type: ACTION_TYPES.FETCH_RENDERERS,
        payload: axiosInstance.get(requestUrl)
    });
};

export const postRenderer = () => async (dispatch) => {
    const requestUrl = callflowsPath + 'renderers';
    const data = [
        {
            name: 'test1',
            mimeType: 'testMimeType',
            template: 'testTemplate'
        }
    ];
    await dispatch({
      type: ACTION_TYPES.CREATE_RENDERER,
      payload: axiosInstance.post(requestUrl, data)  
    });
};