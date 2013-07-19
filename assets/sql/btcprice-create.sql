DROP TABLE IF EXISTS _transactions;

CREATE TABLE _transactions (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    _type TEXT NOT NULL,
    _amount REAL NOT NULL,
    _counter TEXT NOT NULL,
    _base TEXT NOT NULL,
    _price REAL NOT NULL,
    _timestamp TEXT NOT NULL,
    _tx_id TEXT NOT NULL
);
