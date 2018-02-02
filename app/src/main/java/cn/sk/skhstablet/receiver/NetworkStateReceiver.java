package cn.sk.skhstablet.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;

import cn.sk.skhstablet.app.AppConstants;
import cn.sk.skhstablet.tcp.utils.Callback;
import cn.sk.skhstablet.tcp.utils.TcpUtils;
import cn.sk.skhstablet.utlis.NetworkHelper;
import rx.Subscription;

import static cn.sk.skhstablet.app.AppConstants.isLoginOther;
import static cn.sk.skhstablet.app.AppConstants.netState;
import static cn.sk.skhstablet.tcp.utils.TcpUtils.closeConnection;
import static cn.sk.skhstablet.tcp.utils.TcpUtils.fetchData;
import static cn.sk.skhstablet.tcp.utils.TcpUtils.invoke;
import static cn.sk.skhstablet.tcp.utils.TcpUtils.logoutUnsubscribe;
import static cn.sk.skhstablet.tcp.utils.TcpUtils.reconnect;
import static cn.sk.skhstablet.tcp.utils.TcpUtils.setConnDisable;

//网络状态改变监听器，这里其实只是根据网络状态改变发送连接请求，可能会有问题，因为不能得到及时的响应，只有在用户手动修改网络连接状态时才会执行重新连接，建议可以在用户发送命令失败后尝试重新连接
public class NetworkStateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction() == ConnectivityManager.CONNECTIVITY_ACTION) {
			//如果和服务器ping不通
			if(!(NetworkHelper.isAvailableByPing()))
			{
				Toast.makeText(context, "连接服务器失败", Toast.LENGTH_LONG).show();
				setConnDisable();
			}
			else
			{
				//如果网络为未连接且不为在其他设备登录，重新连接
				if(netState==AppConstants.STATE_DIS_CONN&&!isLoginOther)
				{
					/*invoke(TcpUtils.connect(AppConstants.url, AppConstants.port), new Callback<Boolean>() {
						@Override
						public void onResponse(Boolean data) {
							if(data)
							{
								fetchData();//注册链路观察者
								this.unsubscribe();//取消订阅
							}
						}
					});*/
					reconnect();
					Toast.makeText(context, "连接服务器成功", Toast.LENGTH_LONG).show();
				}
			}
		}
	}

}
