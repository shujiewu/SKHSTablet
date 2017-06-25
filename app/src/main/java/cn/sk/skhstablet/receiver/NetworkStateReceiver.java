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

public class NetworkStateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction() == ConnectivityManager.CONNECTIVITY_ACTION) {
			/*if (!(NetworkHelper.isNetworkConnected(context))) {
				Toast.makeText(context, "无网络连接", Toast.LENGTH_LONG).show();
			}*/
			if(!(NetworkHelper.isAvailableByPing()))
			{
				Toast.makeText(context, "连接服务器失败", Toast.LENGTH_LONG).show();
				setConnDisable();
			}
			else
			{
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
