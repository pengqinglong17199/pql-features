// package com.syzl.feature.wechat;
//
// import hyuga.util.wechat.data.WeChatNewsData;
// import hyuga.util.wechat.data.WeChatTextData;
// import hyuga.util.wechat.data.WeChatTextcardData;
// import hyuga.util.wechat.transmitter.WeChatDataTransmitter;
//
// /**
//  * WeChatSendUtil
//  *
//  * @author hyuga
//  * @date 2019-12-27 14:41
//  */
// public class WeChatSend {
//
//     public static void main(String[] args) {
//         // 应用id
//         long applicationId = 1000002;
//         // 企业id
//         String enterpriseId = "ww29126fd6bc20c0bb";
//         // 应用编号
//         String applicationSecret = "sZ0KJAK51H2kQXLQYI8xvSuHiL3u052UEVk1GJzmR3c";
//         String receiverMemberAccount = "HuangZeYuan";
//
//         WeChatDataTransmitter weChatDataTransmitter = WeChatSend.init(enterpriseId, applicationSecret);
//
//         //纯文本消息
//         WeChatTextData weChatTextData = new WeChatTextData(applicationId);
//         weChatTextData.setReceiverMemberAccount(receiverMemberAccount);
//         weChatTextData.setText("这是一条纯文本测试信息");
//
//         //卡片文本消息
//         WeChatTextcardData weChatTextcardData = new WeChatTextcardData(applicationId);
//         weChatTextcardData.setReceiverMemberAccount(receiverMemberAccount);
//         weChatTextcardData.setTitle("卡片文本消息标题");
//         weChatTextcardData.setDescription("卡片文本消息内容");
//         weChatTextcardData.setUrl("http://google.com");
//         weChatTextcardData.setBtntxt("详情查看");
//
//         //图文消息
//         WeChatNewsData weChatNewsData = new WeChatNewsData(applicationId);
//         weChatNewsData.setReceiverMemberAccount(receiverMemberAccount);
//         weChatNewsData.setTitle("图文消息标题");
//         weChatNewsData.setDescription("图文消息内容");
//         weChatNewsData.setUrl("http://google.com");
//         weChatNewsData.setPicurl("https://mmbiz.qpic.cn/mmbiz_jpg/eQPyBffYbucdkseevstNn8P1Juvpb79POdGU0CvvdLMB1uInn8q5dY8ZPthgiao72QcHPjasR97EZibYerJVwzJQ/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1");
//
//         String weChatTextDataResp = weChatDataTransmitter.send(weChatTextData);
//         String weChatTextCardDataResp = weChatDataTransmitter.send(weChatTextcardData);
//         String weChatNewsDataResp = weChatDataTransmitter.send(weChatNewsData);
//         System.out.println("发送微信纯文本消息的响应数据======>" + weChatTextDataResp);
//         System.out.println("发送微信卡片文本消息的响应数据======>" + weChatTextCardDataResp);
//         System.out.println("发送微信图文消息的响应数据======>" + weChatNewsDataResp);
//     }
//
//     public WeChatDataTransmitter init(String enterpriseId, String applicationSecret) {
//         return new WeChatDataTransmitter(enterpriseId, applicationSecret);
//     }
//
// }
