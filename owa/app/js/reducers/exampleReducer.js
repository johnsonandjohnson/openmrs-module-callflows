export const ACTION_TYPES = {
  RESET: "exampleReducer/RESET"
};

const initialState = {
  errorMessage: null
};

export default (state = initialState, action) => {
  switch (action.type) {
    case ACTION_TYPES.RESET: {
      return {
        ...state,
        errorMessage: null
      };
    }
    default:
      return state;
  }
};
