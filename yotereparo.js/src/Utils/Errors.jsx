export const processErrors = incomingErrors => {
  let errors = [];

  if (incomingErrors.data.length >= 1) {
    incomingErrors.data.forEach(error => {
      errors.push({
        type: error.field,
        message: error.defaultMessage
      });
    });
  } else {
    errors.push({
      type: incomingErrors.data.field,
      message: incomingErrors.data.defaultMessage
    });
  }

  return errors;
};
