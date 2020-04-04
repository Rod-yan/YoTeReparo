export const processErrors = (incomingErrors) => {
  let errors = [];

  if (incomingErrors.length >= 1) {
    incomingErrors.forEach((error) => {
      errors.push({
        type: error.field,
        message: error.defaultMessage,
      });
    });
  } else {
    errors.push({
      type: incomingErrors.field,
      message: incomingErrors.defaultMessage,
    });
  }

  return errors;
};
