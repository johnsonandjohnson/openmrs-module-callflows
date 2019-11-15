import _ from 'lodash';

// Form validation method based on Yup schema validation

export const validateForm = (form, schema) => new Promise((resolve, reject) => {
  schema.validate(form, {
      abortEarly: false
    })
    .then(() => {
      resolve(form);
    })
    .catch(function (ex) {
      let errors = {};
      ex.inner.map((e) => {
        if (!!e.path) {
          errors[e.path] = e.errors[0];
        }
      });
      reject(errors);
    });
});

export const validateField  = (form, fieldPath, schema) => new Promise((resolve, reject) => {
  if (_.get(schema.fields, fieldPath) === undefined) {
    return resolve(fieldPath);
  }
  schema.validateAt(fieldPath, form)
    .then(() => {
      resolve(fieldPath);
    })
    .catch(function (ex) {
      let errors = {};
      errors[ex.path] = ex.errors[0];
      reject(errors);
    });
});
