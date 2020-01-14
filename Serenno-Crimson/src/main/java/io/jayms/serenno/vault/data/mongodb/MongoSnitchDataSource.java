package io.jayms.serenno.vault.data.mongodb;

import com.mongodb.client.MongoCollection;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementDataSource;
import io.jayms.serenno.model.citadel.snitch.Snitch;
import io.jayms.serenno.model.citadel.snitch.SnitchDataSource;
import io.jayms.serenno.util.Coords;
import org.bson.Document;
import org.bson.conversions.Bson;

public interface MongoSnitchDataSource extends SnitchDataSource {

    MongoCollection<Document> getCollection();

    Snitch fromDocument(Document doc);

    Document toDocument(Snitch val);

    Bson getFilter(Reinforcement key);

}