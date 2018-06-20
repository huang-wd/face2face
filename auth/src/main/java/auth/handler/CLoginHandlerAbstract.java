package auth.handler;

import auth.AbstractImHandler;
import auth.Worker;
import auth.utils.Common;
import auth.utils.RouteUtil;
import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protobuf.generate.cli2srv.login.Auth;
import thirdparty.redis.utils.UserUtils;
import thirdparty.thrift.generate.db.user.Account;
import thirdparty.thrift.utils.DBOperator;

/**
 * Created by win7 on 2016/3/3.
 */
public class CLoginHandlerAbstract extends AbstractImHandler {
    private static final Logger logger = LoggerFactory.getLogger(CLoginHandlerAbstract.class);

    public CLoginHandlerAbstract(String userid, long netid, Message msg, ChannelHandlerContext ctx) {
        super(userid, netid, msg, ctx);
    }

    @Override
    protected void execute(Worker worker) throws TException {
        Auth.CLogin msg = (Auth.CLogin)_msg;
        Account account;

        if(!_jedis.exists(UserUtils.genDBKey(_userId))) {
            RouteUtil.sendResponse(Common.ACCOUNT_INEXIST, "Account not exists", _netId, _userId);
            logger.info("Account not exists, userid: {}", _userId);
            return;
        } else {
            byte[] userIdBytes = _jedis.hget(UserUtils.genDBKey(_userId), UserUtils.userFileds.Account.field);
            account = DBOperator.Deserialize(new Account(), userIdBytes);
        }

        if(account.getUserid().equals(_userId) && account.getPasswd().equals(msg.getPasswd())) {
            AuthServerHandler.putInUserIdMap(_userId, _netId);
            RouteUtil.sendResponse(Common.VERYFY_PASSED, "Verify passed", _netId, _userId);
            logger.info("userid: {} verify passed", _userId);
        } else {
            RouteUtil.sendResponse(Common.VERYFY_ERROR, "Account not exist or passwd error", _netId, _userId);
            logger.info("userid: {} verify failed", _userId);
            return;
        }
    }
}
