import * as Yup from 'yup';
import * as Msg from '../utils/messages';

export interface IFlowTestError {
  configuration?: string,
  extension?: string,
  phoneNumber?: string,
}

export const validationSchema = Yup.object().shape({
  configuration: Yup.string()
    .required(Msg.FIELD_REQUIRED),
  extension: Yup.string()
    .required(Msg.FIELD_REQUIRED),
  phoneNumber: Yup.string()
    .required(Msg.FIELD_REQUIRED)
});
