package io.jayms.serenno.vault.data.mongodb;

import com.mongodb.client.MongoCollection;
import io.jayms.serenno.model.citadel.bastion.Bastion;
import io.jayms.serenno.model.citadel.bastion.BastionDataSource;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementDataSource;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.checkerframework.checker.units.qual.K;

public interface MongoBastionDataSource extends BastionDataSource {

    MongoCollection<Document> getCollection();

    Bastion fromDocument(Document doc);

    Document toDocument(Bastion val);

    Bson getFilter(Reinforcement key);

}
