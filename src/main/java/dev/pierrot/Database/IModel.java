package dev.pierrot.Database;

import org.bson.Document;

public abstract class IModel {
    public abstract Document toDocument();
}
