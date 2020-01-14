package io.jayms.serenno.vault.data.mongodb;

import com.mongodb.client.MongoCollection;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementDataSource;
import io.jayms.serenno.util.Coords;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.checkerframework.checker.units.qual.K;

public interface MongoReinforcementDataSource extends ReinforcementDataSource {

    MongoCollection<Document> getCollection();

    Reinforcement fromDocument(Document doc);

    Document toDocument(Reinforcement val);

    Bson getFilter(Coords key);

}
