import * as Yup from 'yup';
import * as Default from '../utils/messages';
import { getIntl } from "@openmrs/react-components/lib/components/localization/withLocalization";

export interface IFlowCallError {
  configuration?: string,
  extension?: string,
  phoneNumber?: string,
}

export const validationSchema = Yup.object().shape({
  configuration: Yup.string()
    .required(getIntl().formatMessage({ id: 'CALLFLOW_FIELD_REQUIRED', defaultMessage: Default.FIELD_REQUIRED })),
  extension: Yup.string()
    .required(getIntl().formatMessage({ id: 'CALLFLOW_FIELD_REQUIRED', defaultMessage: Default.FIELD_REQUIRED })),
  phoneNumber: Yup.string()
    .required(getIntl().formatMessage({ id: 'CALLFLOW_FIELD_REQUIRED', defaultMessage: Default.FIELD_REQUIRED }))
});
