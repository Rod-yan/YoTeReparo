export const validateEmail = (event, account) => {
  const emailRex = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
  if (emailRex.test(event.target.value)) {
    account["validate"].emailState = "success";
  } else {
    account["validate"].emailState = "danger";
  }
};

export const validatePassword = (event, account) => {
  var strongRegex = new RegExp("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.{8,})");
  if (strongRegex.test(event.target.value)) {
    account["validate"].passwordState = "success";
  } else {
    account["validate"].passwordState = "danger";
  }
};
