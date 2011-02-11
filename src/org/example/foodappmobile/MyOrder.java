package org.example.foodappmobile;

import android.os.Parcel;
import android.os.Parcelable;

public class MyOrder implements Parcelable{
	private String billtofirstname;
	private String billtolastname;
	private String billtoaddress1;
	private String billtocity;
	private String billtostate;
	private String billtozip;
	private String billtocountry;
	private String billtodayphone;
	private String billtonightphone;
	private String billtofax;
	private String billtoemail;
	private String shiptofirstname;
	private String shiptolastname;
	private String shiptoaddress1;
	private String shiptocity;
	private String shiptostate;
	private String shiptozip;
	private String shiptocountry;
	private String shiptodayphone;
	private String shiptonightphone;
	private String shiptofax;
	private String shiptoemail;
	private String comments;
	
	public MyOrder() {}
	
	public void setBillToFirstName(String billtofirstname) {
		this.billtofirstname = billtofirstname;
	}
	
	public void setBillToLastName(String billtolastname) {
		this.billtolastname = billtolastname;
	}
	
	public void setBillToAddress1(String billtoaddress1) {
		this.billtoaddress1 = billtoaddress1;
	}
	
	public void setBillToCity(String billtocity) {
		this.billtocity = billtocity;
	}
	
	public void setBillToState(String billtostate) {
		this.billtostate = billtostate;
	}
	
	public void setBillToZip(String billtozip) {
		this.billtozip = billtozip;
	}
	
	public void setBillToCountry(String billtocountry) {
		this.billtocountry = billtocountry;
	}
	
	public void setBillToDayPhone(String billtodayphone) {
		this.billtodayphone = billtodayphone;
	}
	
	public void setBillToNightPhone(String billtonightphone) {
		this.billtonightphone = billtonightphone;
	}
	
	public void setBillToFax(String billtofax) {
		this.billtofax = billtofax;
	}
	
	public void setBillToEmail(String billtoemail) {
		this.billtoemail = billtoemail;
	}
	
	public void setShipToFirstName(String shiptofirstname) {
		this.shiptofirstname = shiptofirstname;
	}
	
	public void setShipToLastName(String shiptolastname) {
		this.shiptolastname = shiptolastname;
	}
	
	public void setShipToAddress1(String shiptoaddress1) {
		this.shiptoaddress1 = shiptoaddress1;
	}
	
	public void setShipToCity(String shiptocity) {
		this.shiptocity = shiptocity;
	}
	
	public void setShipToState(String shiptostate) {
		this.shiptostate = shiptostate;
	}
	
	public void setShipToZip(String shiptozip) {
		this.shiptozip = shiptozip;
	}
	
	public void setShipToCountry(String shiptocountry) {
		this.shiptocountry = shiptocountry;
	}
	
	public void setShipToDayPhone(String shiptodayphone) {
		this.shiptodayphone = shiptodayphone;
	}
	
	public void setShipToNightPhone(String shiptonightphone) {
		this.shiptonightphone = shiptonightphone;
	}
	
	public void setShipToFax(String shiptofax) {
		this.shiptofax = shiptofax;
	}
	
	public void setShipToEmail(String shiptoemail) {
		this.shiptoemail = shiptoemail;
	}
	
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public String getBillToFirstName() {
		return billtofirstname;
	}
	
	public String getBillToLastName() {
		return billtolastname;
	}
	
	public String getBillToAddress1() {
		return billtoaddress1;
	}
	
	public String getBillToCity() {
		return billtocity;
	}
	
	public String getBillToState() {
		return billtostate;
	}
	
	public String getBillToZip() {
		return billtozip;
	}
	
	public String getBillToCountry() {
		return billtocountry;
	}
	
	public String getBillToDayPhone() {
		return billtodayphone;
	}
	
	public String getBillToNightPhone() {
		return billtonightphone;
	}
	
	public String getBillToFax() {
		return billtofax;
	}
	
	public String getBillToEmail() {
		return billtoemail;
	}
	
	public String getShipToFirstName() {
		return shiptofirstname;
	}
	
	public String getShipToLastName() {
		return shiptolastname;
	}
	
	public String getShipToAddress1() {
		return shiptoaddress1;
	}
	
	public String getShipToCity() {
		return shiptocity;
	}
	
	public String getShipToState() {
		return shiptostate;
	}
	
	public String getShipToZip() {
		return shiptozip;
	}
	
	public String getShipToCountry() {
		return shiptocountry;
	}
	
	public String getShipToDayPhone() {
		return shiptodayphone;
	}
	
	public String getShipToNightPhone() {
		return shiptonightphone;
	}
	
	public String getShipToFax() {
		return shiptofax;
	}
	
	public String getShipToEmail() {
		return shiptoemail;
	}
	
	public String getComments() {
		return comments;
	}
	
	public MyOrder(Parcel in) {
		readFromParcel(in);
	}
	
	public int describeContents() {
		return 0;
	}
	
	public void writeToParcel(Parcel dest, int flags) {
 
		// We just need to write each field into the
		// parcel. When we read from parcel, they
		// will come back in the same order
		dest.writeString(billtofirstname);
		dest.writeString(billtolastname);
		dest.writeString(billtoaddress1);
		dest.writeString(billtocity);
		dest.writeString(billtostate);
		dest.writeString(billtozip);
		dest.writeString(billtocountry);
		dest.writeString(billtodayphone);
		dest.writeString(billtonightphone);
		dest.writeString(billtofax);
		dest.writeString(billtoemail);
		dest.writeString(shiptofirstname);
		dest.writeString(shiptolastname);
		dest.writeString(shiptoaddress1);
		dest.writeString(shiptocity);
		dest.writeString(shiptostate);
		dest.writeString(shiptozip);
		dest.writeString(shiptocountry);
		dest.writeString(shiptodayphone);
		dest.writeString(shiptonightphone);
		dest.writeString(shiptofax);
		dest.writeString(shiptoemail);
		dest.writeString(comments);
		
	}
	
	private void readFromParcel(Parcel in) {
		 
		// We just need to read back each
		// field in the order that it was
		// written to the parcel
		billtofirstname = in.readString();
		billtolastname = in.readString();
		billtoaddress1 = in.readString();
		billtocity = in.readString();
		billtostate = in.readString();
		billtozip = in.readString();
		billtocountry = in.readString();
		billtodayphone = in.readString();
		billtonightphone = in.readString();
		billtofax = in.readString();
		billtoemail = in.readString();
		shiptofirstname = in.readString();
		shiptolastname = in.readString();
		shiptoaddress1 = in.readString();
		shiptocity = in.readString();
		shiptostate = in.readString();
		shiptozip = in.readString();
		shiptocountry = in.readString();
		shiptodayphone = in.readString();
		shiptonightphone = in.readString();
		shiptofax = in.readString();
		shiptoemail = in.readString();
		comments = in.readString();
	}
	
	public static final Parcelable.Creator<MyOrder> CREATOR =
    	new Parcelable.Creator<MyOrder>() {
            public MyOrder createFromParcel(Parcel in) {
                return new MyOrder(in);
            }
            
            public MyOrder[] newArray(int size) {
                return new MyOrder[size];
            }
 
	};
	

}
