DROP TABLE IF EXISTS _trades;

CREATE TABLE _trades (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    _type TEXT NOT NULL,
    _amount REAL NOT NULL,
    _counter TEXT NOT NULL,
    _base TEXT NOT NULL,
    _price REAL NOT NULL,
    _timestamp TEXT NOT NULL,
    _trade_id TEXT NOT NULL
);
