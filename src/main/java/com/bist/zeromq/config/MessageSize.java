package com.bist.zeromq.config;



public enum MessageSize
{
    KB60(0,1024*60),KB300(1,1024*300),
    KB600(2,1024*600),MB3(3,1024 *1024 *3),
    MB30(4,1024 * 1024 *30),TYPE_UNKNOWN(-1,-1);

   private int size;
   private int code;
   MessageSize(int code, int size){
       this.code=code;
    this.size=size;
   }
   public int getSize(){
     return size;
   }



    public static MessageSize getByCode(int code)
    {
        for (MessageSize type : values())
        {
            if (type.getCode() == code)
            {
                return type;
            }
        }
        return TYPE_UNKNOWN;
    }

    public int getCode()
    {
        return code;
    }

}
