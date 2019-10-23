import axiosInstance from '../config/axios';
import { SUCCESS, REQUEST, FAILURE } from './action-type.util';
import { RendererModel } from '../shared/model/Renderer.model';

export const ACTION_TYPES = {
    RESET: 'renderersReducer/RESET',
    FETCH_RENDERERS: 'renderersReducer/FETCH_RENDERERS',
    CREATE_RENDERER: 'renderersReducer/CREATE_RENDERER',
    ADD_NEW_EMPTY: 'rendererReducer/ADD_NEW_EMPTY',
    UPDATE_RENDERER_AFTER_CHANGE: 'rendererReducer/UPDATE_RENDERER_AFTER_CHANGE',
    UPDATE_RENDERER: 'rendererReducer/UPDATE_RENDERER',
    DELETE_FROM_FRONT_RENDERER: 'rendererReducer/DELETE_FROM_FRONT_RENDERER'
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
          let newValue = new RendererModel(action.payload.data, action.isOpen);
          newValue.uiLocalUuid = action.uiLocalUuid;
            return {
                ...state,
                renderers: replaceRenderer(state.renderers, newValue)
            };
        // case SUCCESS(ACTION_TYPES.CREATE_RENDERER):
        //     return {
        //         ...state,
        //         renderers: action.payload.data
        //     }


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
                renderers: action.payload.data.map((rendererResponse) => {return new RendererModel(rendererResponse, false)})
            };


        case REQUEST(ACTION_TYPES.UPDATE_RENDERER):
            return {
                ...state
            }     
        case FAILURE(ACTION_TYPES.UPDATE_RENDERER):
            return {
                ...state
            }   
        case SUCCESS(ACTION_TYPES.UPDATE_RENDERER):
            return {
                ...state
            }

        case ACTION_TYPES.ADD_NEW_EMPTY:
            return {
                ...state,
                renderers: state.renderers.concat(new RendererModel(null, true))
            }

        case ACTION_TYPES.DELETE_FROM_FRONT_RENDERER:
            return {
                ...state,
                renderers: removeFromRenderers(state.renderers, action.uiLocalUuid)
            }    
            
        case ACTION_TYPES.UPDATE_RENDERER_AFTER_CHANGE:
            return {
                ...state,
                renderers: replaceRenderer(state.renderers, action.payload)
            }
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

const replaceRenderer = (renderers, changedRenderer) => {
    return renderers.map((item) => {
        if (item.uiLocalUuid === changedRenderer.uiLocalUuid) {
            item = changedRenderer;
        }
        return item;
    });
}

const removeFromRenderers = (renderers, uiLocalUuid) => {
    return renderers.filter((item) => item.uiLocalUuid !== uiLocalUuid);
}

export const reset = () => ({
    type: ACTION_TYPES.RESET 
});

export const addNew = () => ({
    type: ACTION_TYPES.ADD_NEW_EMPTY
});

export const changeRenderer = (newRenderer) => ({
    type: ACTION_TYPES.UPDATE_RENDERER_AFTER_CHANGE,
    payload: newRenderer
});

export const getRenderers = () => async (dispatch) => {
    const requestUrl = callflowsPath + '/renderers';
    await dispatch({
        type: ACTION_TYPES.FETCH_RENDERERS,
        payload: axiosInstance.get(requestUrl)
    });
};

export const createRenderer = (rendererRequest) => async (dispatch) => {
    const requestUrl = callflowsPath + '/renderers';
    const data = [
        {
            name: rendererRequest.name,
            mimeType: rendererRequest.mimeType,
            template: rendererRequest.template
        }
    ];
    await dispatch({
        type: ACTION_TYPES.CREATE_RENDERER,
        isOpen: rendererRequest.isOpen,
        uiLocalUuid: rendererRequest.uiLocalUuid,
        //payload: axiosInstance.post(requestUrl, rendererRequest.toRequest())
        payload: axiosInstance.post(requestUrl, data)
    });
};


export const deleteRendererFromFE = (rendererRequest) => async (dispatch) => {
    await dispatch({
        type: ACTION_TYPES.DELETE_FROM_FRONT_RENDERER,
        uiLocalUuid: rendererRequest.uiLocalUuid
    });
};


// export const createRenderer = () => async (dispatch) => {
//     const requestUrl = callflowsPath + '/renderers';
//     const data = [
//         {
//             name: 'firstA',
//             mimeType: 'secondA',
//             template: 'thirdA'
//         }
//     ];
//     await dispatch({
//         type: ACTION_TYPES.CREATE_RENDERER,
//         payload: axiosInstance.post(requestUrl, data)
//     });
// };

export const updateRenderer = (rendererRequest) => async (dispatch) => {
    const requestUrl = callflowsPath + '/renderers';
    const data = [
        {
            name: rendererRequest.name,
            mimeType: rendererRequest.mimeType,
            template: rendererRequest.template
        }
    ];
    await dispatch({
        type: ACTION_TYPES.CREATE_RENDERER,
        isOpen: rendererRequest.isOpen,
        uiLocalUuid: rendererRequest.uiLocalUuid,
        //payload: axiosInstance.post(requestUrl, rendererRequest.toRequest())
        payload: axiosInstance.post(requestUrl, data)
    });
};
    
