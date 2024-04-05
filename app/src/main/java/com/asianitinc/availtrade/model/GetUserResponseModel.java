package com.asianitinc.availtrade.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class GetUserResponseModel
{

    @SerializedName("page") private int page;
    @SerializedName("per_page") private int perPage;
    @SerializedName("total") private int total;
    @SerializedName("total_pages") private int totalPages;
    @SerializedName("data") private List<UserModel> data ;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPerPage() {
        return perPage;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<UserModel> getData() {
        return data;
    }

    public void setData(List<UserModel> data) {
        this.data = data;
    }

    public class UserModel
    {
        @SerializedName("id")private Integer id;
        @SerializedName("email")private String email;
        @SerializedName("first_name")private String firstName;
        @SerializedName("last_name")private String lastName;
        @SerializedName("avatar")private String avatar;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }
}
