export const intersect = (arrayA, arrayB) => {
  return arrayA.filter(medio => arrayB.some(value => medio.id == value));
};
