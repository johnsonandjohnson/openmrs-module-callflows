import axiosInstance from '../config/axios';
import { SUCCESS, REQUEST, FAILURE } from './action-type.util';

import { ConfigFormData } from '../components/ConfigForm';

export const ACTION_TYPES = {
  RESET: 'providersReducer/RESET',
  UPDATE_CONFIG_FORMS: 'providersReducer/UPDATE_CONFIG_FORMS',
  FETCH_CONFIGS: 'providersReducer/FETCH_CONFIGS',
  POST_CONFIG: 'providersReducer/POST_CONFIG'
};

const initialState = {
  configForms: []
};

export default (state = initialState, action) => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.POST_CONFIG):
      return {
        ...state
      };
    case FAILURE(ACTION_TYPES.POST_CONFIG):
      return {
        ...state
      };
    case SUCCESS(ACTION_TYPES.POST_CONFIG):
      return {
        ...state,
        configForms: action.payload.data
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
        configForms: action.payload.data.map((fetched) => { return new ConfigFormData(fetched) })
      };
    case ACTION_TYPES.UPDATE_CONFIG_FORMS: {
      return {
        ...state,
        configForms: updateConfigForms(state.configForms, action.payload)
      };
    }
    case ACTION_TYPES.RESET: {
      return {
        ...state,
        configForms: []
      };
    }
    default:
      return state;
  }
};

const updateConfigForms = (configForms, updated) => {
  return configForms.map((item) => {
    if (item.localId === updated.localId) {
      item = updated;
    }
    return item;
  });
}

export const updateConfigForm = (updated) => ({
  type: ACTION_TYPES.UPDATE_CONFIG_FORMS,
  payload: updated
});

export const reset = () => ({
  type: ACTION_TYPES.RESET
});

const callflowsPath = 'ws/callflows';

export const postConfigs = () => async (dispatch) => {
  const requestUrl = callflowsPath + '/configs';
  const data = [
    {
      "name": "IMI_Mobile",
      "outgoingCallMethod": "POST",
      "outgoingCallPostHeadersMap": {
        "Key": "c8ce658c-68ec-4cf7-bc69-03f9e6658326",
        "Content-Type": "application/x-www-form-urlencoded"
      },
      "outgoingCallPostParams": "address=[phone]&mode=Callflow&schedule_datetime=&patternId=-1&sendername=4071012395&callflow_id=6156&externalParams=_esb_trans_id,_errorcode,uuid&externalHeaders=x-imi-ivrs-uuid:[internal.callId];x-imi-ivrs-numbertodial:[phone];x-imi-ivrs-jumpto:[internal.jumpTo];x-imi-ivrs-callid:[internal.callId]&menu=/openhouse/app/dvp/callflow/callflow_6156/menu_text/funct_cfldemo.txt&callbackurl=http://eapps.imimobile.com/JEET_TB_STG/Dropnotify?",
      "outgoingCallUriTemplate": "http://api-openhouse.imimobile.com/1/intobd/thirdpartycall/callSessions",
      "servicesMap": {
        "patientSrvc": "com.janssen.connectforlife.patient.service.PatientService",
        "visitSrvc": "com.janssen.connectforlife.repository.VisitDataService",
        "healthTipSrvc": "com.janssen.connectforlife.patient.service.HealthTipService",
        "symptomSrvc": "com.janssen.connectforlife.symptomreporting.service.SymptomReportService",
        "patientSymptomSrvc": "com.janssen.connectforlife.patient.service.PatientSymptomService",
        "adherenceSrvc": "com.janssen.connectforlife.patient.service.AdherenceService",
        "diseaseSrvc": "com.janssen.connectforlife.repository.DiseaseDataService",
        "visitService": "com.janssen.connectforlife.patient.service.VisitService",
        "healthTipDataSrvc": "com.janssen.connectforlife.repository.HealthTipDataService",
        "configSrvc": "com.janssen.connectforlife.config.service.ConfigService",
        "reminderSrvc": "com.janssen.connectforlife.patient.service.ReminderService",
        "treatmentDataSrvc": "com.janssen.connectforlife.repository.TreatmentDataService",
        "outboxDataSrvc": "com.janssen.connectforlife.repository.PatientOutboxMessageDataService",
        "adherenceReportDataService": "com.janssen.connectforlife.repository.AdherenceReportDataService",
        "patientAlertSrvc": "com.janssen.connectforlife.patient.service.PatientAlertService",
        "ruleDataSrvc": "com.janssen.connectforlife.symptomreporting.repository.RuleDataService",
        "patientActionLogSrvc": "com.janssen.connectforlife.patient.service.PatientActionLogService",
        "caregiverService": "com.janssen.connectforlife.patient.service.CaregiverService",
        "patientCaregiverService": "com.janssen.connectforlife.patient.service.PatientCaregiverService",
        "caregiverDataSrvc": "com.janssen.connectforlife.repository.CaregiverDataService"
      },
      "outboundCallLimit": 0,
      "outboundCallRetryAttempts": 0,
      "outboundCallRetrySeconds": 0,
      "callAllowed": true,
      "testUsersMap": {},
      hasAuthRequired: false
    },
    {
      "name": "X",
      "outgoingCallMethod": "GET",
      "outgoingCallPostHeadersMap": {},
      "outgoingCallPostParams": "",
      "outgoingCallUriTemplate": "",
      "servicesMap": {},
      "outboundCallLimit": 0,
      "outboundCallRetryAttempts": 0,
      "outboundCallRetrySeconds": 0,
      "callAllowed": true,
      "testUsersMap": {},
      hasAuthRequired: false
    },
    {
      "name": "test1",
      "outgoingCallMethod": "GET",
      "outgoingCallPostHeadersMap": {},
      "outgoingCallPostParams": "",
      "outgoingCallUriTemplate": "",
      "servicesMap": {},
      "outboundCallLimit": 0,
      "outboundCallRetryAttempts": 0,
      "outboundCallRetrySeconds": 0,
      "callAllowed": true,
      "testUsersMap": {},
      hasAuthRequired: false
    }
  ]; // ToDo add real data data
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
