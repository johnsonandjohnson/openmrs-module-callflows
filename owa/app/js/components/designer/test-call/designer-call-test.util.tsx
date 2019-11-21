import * as Msg from '../../../shared/utils/messages';
import { toast } from 'react-toastify';
import '../../../../css/toast.scss';
import { ToastStatusContent, CloseButton } from '../../../shared/utils/toast-builder-util';
import { TOAST_CLASS, CLOSE_DELAY, CALLFLOW_TEST_SUCCESS_STATUS } from '../../../constants';
import * as React from 'react';

const errorMessage = Msg.DESIGNER_TEST_CALL_INITIATION_FAILURE;

export const handleTestCallRequest = async (dispatch, body) => {

  var toastId = toast(
    <ToastStatusContent message={Msg.GENERIC_PROCESSING} type="notice" />,
    {
      autoClose: false,
      closeButton: false,
      className: TOAST_CLASS,
      hideProgressBar: true
    }
  );
  body.meta = toastId;
  try {
    await dispatch(body);
  } catch(e) {
    try {
      toast.update(toastId, {
        render: <ToastStatusContent message={[errorMessage, e.response.data.message].join(' ')} type={toast.TYPE.ERROR} />,
        autoClose: CLOSE_DELAY,
        closeButton: <CloseButton />,
        className: TOAST_CLASS,
        hideProgressBar: true
      });
    } catch(e) {
      toast.update(toastId, {
        render: <ToastStatusContent message={Msg.GENERIC_FAILURE} type={toast.TYPE.ERROR} />,
        autoClose: CLOSE_DELAY,
        closeButton: <CloseButton />,
        className: TOAST_CLASS,
        hideProgressBar: true
      });
    }
  }
}

export const handleSuccessMessage = (response: any, toastId: any) => {
  try {
    if (response.status === CALLFLOW_TEST_SUCCESS_STATUS) {
      toast.update(toastId, {
        render: <ToastStatusContent message={Msg.DESIGNER_TEST_CALL_INITIATION_SUCCESS} type={toast.TYPE.SUCCESS} />,
        autoClose: CLOSE_DELAY,
        closeButton: <CloseButton />,
        className: TOAST_CLASS,
        hideProgressBar: true
      });
    } else {
      toast.update(toastId, {
        render: <ToastStatusContent message={[errorMessage, response.reason].join(' ')} type={toast.TYPE.ERROR} />,
        autoClose: CLOSE_DELAY,
        closeButton: <CloseButton />,
        className: TOAST_CLASS,
        hideProgressBar: true
      });
    }
  } catch(ex) {
    toast.update(toastId, {
      render: <ToastStatusContent message={Msg.GENERIC_FAILURE} type={toast.TYPE.ERROR} />,
      autoClose: CLOSE_DELAY,
      closeButton: <CloseButton />,
      className: TOAST_CLASS,
      hideProgressBar: true
    });
  }
}
