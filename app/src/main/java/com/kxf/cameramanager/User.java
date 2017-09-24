package com.kxf.cameramanager;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "User")
public class User {
	@Column(name = "id", isId = true)
	private int id;
	@Column(name = "name")
	private String name;
	@Column(name = "sex")
	private String sex;
	@Column(name = "age")
	private int age;
	@Column(name = "tel")
	private String tel;
	@Column(name = "address")
	private String address;
	@Column(name = "info")
	private String info;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", name='" + name + '\'' +
				", sex='" + sex + '\'' +
				", age=" + age +
				", tel='" + tel + '\'' +
				", address='" + address + '\'' +
				", info='" + info + '\'' +
				'}';
	}
}
