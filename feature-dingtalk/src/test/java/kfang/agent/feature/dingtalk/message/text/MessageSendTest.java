// package kfang.agent.feature.dingtalk.message.text;
//
// import cn.hyugatool.core.collection.ListUtil;
// import cn.hyugatool.core.properties.PropertiesUtil;
// import kfang.agent.feature.dingtalk.message.*;
// import kfang.agent.feature.dingtalk.util.DingTalkUtil;
// import org.junit.Test;
//
// import java.io.IOException;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Properties;
//
// /**
//  * MessageSendTest
//  *
//  * @author hyuga
//  * @since 2022-01-19 17:06:21
//  */
// public class MessageSendTest {
//
//     @Test
//     public void test() throws IOException {
//         Properties fileProperties = PropertiesUtil.getFilePropertiesInSourceModule("ding-pro.properties");
//         System.out.println(fileProperties.getProperty("accessToken"));
//         System.out.println(fileProperties.getProperty("secret"));
//     }
//
//     @Test
//     public void text() throws Exception {
//         CoreParameter coreParameter = CoreParameter.builder()
//                 .accessToken("446cfbe11456505cf01ac32f26bf88748395b1761121d991105154134244e35b")
//                 .secret("SECc4c82c1fc0fc9e171682236c330c3245553bc18f887d1a215daf3aae1b6c479a")
//                 // .isAtAll(true)
//                 // .atUserIds(ListUtil.newArrayList("13f_j2p8f7iido"))
//                 .atMobiles(ListUtil.newArrayList("18814128895"))
//                 .build();
//
//         MessageText text = new MessageText(coreParameter);
//         text.setText("agent \n exception message.");
//         DingTalkUtil.sendMessage(text);
//     }
//
//     @Test
//     public void markdown() throws Exception {
//         CoreParameter coreParameter = CoreParameter.builder()
//                 .accessToken("446cfbe11456505cf01ac32f26bf88748395b1761121d991105154134244e35b")
//                 .secret("SECc4c82c1fc0fc9e171682236c330c3245553bc18f887d1a215daf3aae1b6c479a")
//                 // .isAtAll(true)
//                 // .atUserIds(ListUtil.newArrayList("13f_j2p8f7iido"))
//                 .atMobiles(ListUtil.newArrayList("18814128895"))
//                 .build();
//
//         MessageMarkdown markdown = new MessageMarkdown(coreParameter);
//         markdown.setTitle("标题");
//         markdown.setText("- agent exception message.\n- xxxx");
//         DingTalkUtil.sendMessage(markdown);
//     }
//
//     @Test
//     public void link() throws Exception {
//         CoreParameter coreParameter = CoreParameter.builder()
//                 .accessToken("446cfbe11456505cf01ac32f26bf88748395b1761121d991105154134244e35b")
//                 .secret("SECc4c82c1fc0fc9e171682236c330c3245553bc18f887d1a215daf3aae1b6c479a")
//                 // .isAtAll(true)
//                 // .atUserIds(ListUtil.newArrayList("13f_j2p8f7iido"))
//                 // .atMobiles(ListUtil.newArrayList("18814128895"))
//                 .build();
//
//         MessageLink link = new MessageLink(coreParameter);
//         link.setTitle("标题");
//         link.setText("agent exception message.");
//         link.setPicUrl("https://pic1.zhimg.com/v2-66fedbd3b7051cbd68a54c090a64f9c8_r.jpg");
//         link.setMessageUrl("http://www.baidu.com");
//         //DingTalkUtil.sendMessage(link);
//     }
//
//     @Test
//     public void activeCard() throws Exception {
//         CoreParameter coreParameter = CoreParameter.builder()
//                 .accessToken("446cfbe11456505cf01ac32f26bf88748395b1761121d991105154134244e35b")
//                 .secret("SECc4c82c1fc0fc9e171682236c330c3245553bc18f887d1a215daf3aae1b6c479a")
//                 // .isAtAll(true)
//                 // .atUserIds(ListUtil.newArrayList("13f_j2p8f7iido"))
//                 .atMobiles(ListUtil.newArrayList("18814128895"))
//                 .build();
//
//         MessageActionCard activeCard = new MessageActionCard(coreParameter);
//         activeCard.setTitle("标题");
//         activeCard.setText("agent exception message.");
//         activeCard.setSingleTitle("Single标题");
//         activeCard.setSingleURL("http://www.baidu.com");
//         //DingTalkUtil.sendMessage(activeCard);
//     }
//
//     @Test
//     public void feedcard() throws Exception {
//         CoreParameter coreParameter = CoreParameter.builder()
//                 .accessToken("446cfbe11456505cf01ac32f26bf88748395b1761121d991105154134244e35b")
//                 .secret("SECc4c82c1fc0fc9e171682236c330c3245553bc18f887d1a215daf3aae1b6c479a")
//                 .isAtAll(true)
//                 // .atUserIds(ListUtil.newArrayList("13f_j2p8f7iido"))
//                 // .atMobiles(ListUtil.newArrayList("18814128895"))
//                 .build();
//
//         MessageFeedcard feedcard = new MessageFeedcard(coreParameter);
//
//         List<MessageFeedcard.MessageFeedCardItem> feedCardItems = new ArrayList<>();
//         for (int i = 0; i < 2; i++) {
//             MessageFeedcard.MessageFeedCardItem messageFeedCardItem = new MessageFeedcard.MessageFeedCardItem();
//             messageFeedCardItem.setTitle("标题" + i);
//             messageFeedCardItem.setPicURL("https://pic1.zhimg.com/v2-66fedbd3b7051cbd68a54c090a64f9c8_r.jpg");
//             messageFeedCardItem.setMessageURL("http://www.baidu.com");
//             feedCardItems.add(messageFeedCardItem);
//         }
//
//         feedcard.setFeedCardItems(feedCardItems);
//         //DingTalkUtil.sendMessage(feedcard);
//     }
//
// }
