/***************************************************************************
 * File:  PojoListener.java Course materials (23W) CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
 *
 */
package acmecollege.entity;

import java.time.LocalDateTime;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

@SuppressWarnings("unused")

public class PojoListener {

	// TODO xPL01 - What annotation is used when we want to do something just before object is INSERT'd in the database?
	@PrePersist
	public void setCreatedOnDate(PojoBase pojoBase) {
		LocalDateTime now = LocalDateTime.now();
		// TODO xPL02 - What member field(s) do we wish to alter just before object is INSERT'd in the database?
		pojoBase.setCreated(now);
		pojoBase.setUpdated(now);
	}
	@PreUpdate
	// TODO xPL03 - What annotation is used when we want to do something just before object is UPDATE'd in the database?
	public void setUpdatedDate(PojoBase pojoBase) {
		// TODO xPL04 - What member field(s) do we wish to alter just before object is UPDATE'd in the database?
		pojoBase.setUpdated(LocalDateTime.now());
	}

}
