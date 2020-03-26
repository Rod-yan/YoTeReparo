const crypto = require("crypto");

module.exports = class HashMap {
  constructor() {
    this.table = [];
  }

  getHash(itemKey) {
    let hash = crypto.createHash("sha256");
    hash.update(itemKey);
    return hash.digest("hex");
  }

  push(item) {
    if (typeof item === undefined) {
      throw new Error("ERROR: Not undefined allowed");
    }

    let hash = this.getHash(item.toString());

    if (this.table[hash] === undefined) {
      this.table[hash] = [{ id: this.table.length, item }];
    } else {
      this.table[hash] = [
        ...this.table[hash],
        { id: this.table[hash].length, item }
      ];
    }

    return this.table[hash];
  }

  pop(item) {
    let hash = this.getHash(item.toString());
    delete this.table[hash];
    return this.table.slice(-1)[0];
  }

  get(item) {
    let hash = this.getHash(item.toString());
    if (this.table[hash] !== undefined) {
      return this.table[hash];
    } else {
      return -1;
    }
  }
};
