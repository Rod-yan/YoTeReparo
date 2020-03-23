export const intersect = (arrayA, arrayB) => {
  try {
    return arrayA.filter(medio => arrayB.some(value => medio.id === value));
  } catch (e) {
    throw new Error(e);
  }
};

export const atoi = str => {
  const number = parseInt(
    String(str)
      .trim()
      .split()[0]
  );
  return Number.isInteger(number) ? Math.max(-(2 ** 31), number) : 0;
};

export const clean = array => {
  const fixLength = array.length;

  for (let index = 0; index <= fixLength; index++) {
    array = array.splice(0, 1);
  }

  return array;
};
