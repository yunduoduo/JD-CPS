package com.raincc.robot.entity;

import java.util.List;

import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.TableMapping;

@SuppressWarnings("serial")
public abstract class BaseModel<M extends Model<M>> extends Model<M> {
	
	protected static final Logger _log = Logger.getLogger(BaseModel.class);
	
	public boolean saveOrUpdate() {
		if (this.get(TableMapping.me().getTable(this.getClass()).getPrimaryKey()) == null) {
			return this.save();
		} else {
			return this.update();
		}
	}
	
	public boolean enableSave() {
		if (this.get(TableMapping.me().getTable(this.getClass()).getPrimaryKey()) == null) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 
	 * @param sort, true-asc, false-desc
	 * @return
	 */
	public List<M> findAll(boolean sort) {
		return this.find("select * from " + TableMapping.me().getTable(this.getClass()).getName() + " order by " + TableMapping.me().getTable(this.getClass()).getPrimaryKey() + (sort ? " asc" : " desc"));
	}

}
