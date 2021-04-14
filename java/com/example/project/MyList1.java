package com.example.project;

public class MyList1 {
    private String text;
    private String no;

    public MyList1(String text) {
        this.text=text;
        no="0";
    }
    public MyList1(String text,String no){
        this.text=text;
        this.no=no;
    }
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getNo() {
        return no;
    }
    public void setNo(String no){this.no=no;}
}
