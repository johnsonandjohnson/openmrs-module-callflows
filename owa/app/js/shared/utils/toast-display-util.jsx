
import { ToastStatusContent, CloseButton } from './toast-builder-util';
import { toast } from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
import '../../../css/toast.scss';

const CLOSE_DELAY = 5000;
const TOAST_CLASS = 'toast-item';

export const successToast = message => displayToast(message, toast.TYPE.SUCCESS);

export const errorToast = message => displayToast(message, toast.TYPE.ERROR);

export const displayToast = (message, type) =>
  toast(
    <ToastStatusContent {... { message, type }} />,
    {
      autoClose: CLOSE_DELAY,
      closeButton: <CloseButton />,
      className: TOAST_CLASS,
      hideProgressBar: true
    });
