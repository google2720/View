package com.example.administrator.opensouce;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * ITEM的对应可序化队列属性
 *  */
public class ChannelItem implements  Parcelable {

	/**
	 * 栏目对应ID
	 *  */
	public Integer id;
	/**
	 * 栏目对应NAME
	 *  */
	public String name;
	/**
	 * 栏目在整体中的排序顺序  rank
	 *  */
	public Integer orderId;
	/**
	 * 栏目是否选中
	 *  */
	public Integer selected;

	public ChannelItem() {
	}

	public ChannelItem(int id, String name, int orderId,int selected) {
		this.id = Integer.valueOf(id);
		this.name = name;
		this.orderId = Integer.valueOf(orderId);
		this.selected = Integer.valueOf(selected);
	}

	public int getId() {
		return this.id.intValue();
	}

	public String getName() {
		return this.name;
	}

	public int getOrderId() {
		return this.orderId.intValue();
	}

	public Integer getSelected() {
		return this.selected;
	}

	public void setId(int paramInt) {
		this.id = Integer.valueOf(paramInt);
	}

	public void setName(String paramString) {
		this.name = paramString;
	}

	public void setOrderId(int paramInt) {
		this.orderId = Integer.valueOf(paramInt);
	}

	public void setSelected(Integer paramInteger) {
		this.selected = paramInteger;
	}

	public String toString() {
		return "ChannelItem [id=" + this.id + ", name=" + this.name
				+ ", selected=" + this.selected + "]";
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeValue(this.id);
		dest.writeString(this.name);
		dest.writeValue(this.orderId);
		dest.writeValue(this.selected);
	}

	protected ChannelItem(Parcel in) {
		this.id = (Integer) in.readValue(Integer.class.getClassLoader());
		this.name = in.readString();
		this.orderId = (Integer) in.readValue(Integer.class.getClassLoader());
		this.selected = (Integer) in.readValue(Integer.class.getClassLoader());
	}

	public static final Parcelable.Creator<ChannelItem> CREATOR = new Parcelable.Creator<ChannelItem>() {
		@Override
		public ChannelItem createFromParcel(Parcel source) {
			return new ChannelItem(source);
		}

		@Override
		public ChannelItem[] newArray(int size) {
			return new ChannelItem[size];
		}
	};
}