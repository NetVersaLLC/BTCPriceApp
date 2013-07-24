DROP TABLE IF EXISTS _transactions;

CREATE TABLE _transactions (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    _type TEXT NOT NULL, -- transaction type that was on the books (bid/ask)
    _exchange TEXT NOT NULL,
    _amount REAL NOT NULL,
    _counter TEXT NOT NULL, -- currency price is listed in
    _base TEXT NOT NULL, -- commodity being bought or sold
    _price REAL NOT NULL,
    _timestamp TEXT NOT NULL,
    _tx_id TEXT NOT NULL -- exchange-specific identifier, useful for making requests related to this transaction
);

DROP TABLE IF EXISTS _price_change_thresholds;

CREATE TABLE _price_change_thresholds (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    _type TEXT NOT NULL, -- absolute or relative to last price change
    _exchange TEXT NOT NULL,
    _amount REAL NOT NULL
);

DROP TABLE IF EXISTS _price_change_bases;

CREATE TABLE _price_change_bases (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    _exchange TEXT NOT NULL,
    _amount REAL NOT NULL
);
