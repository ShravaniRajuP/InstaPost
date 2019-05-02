package com.shravanirajup.instapost;

public class Upload {

    public String mName;

    public String mImageUrl;

    public String mHashtag;

    public String mComments;

    public String mNickName;

    public Upload() { }

    public Upload(String name,String url,String Hashtag,String comments,String nickName) {
        mName=name;
        mImageUrl=url;
        mHashtag=Hashtag;
        mNickName=nickName;
        mComments=comments;
    }
}