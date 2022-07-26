package nova.committee.talismans.common.morph.cap;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/26 12:22
 * Version: 1.0
 */
public class ProxyEntityCapability implements IProxyEntityCapability
{
    private boolean proxy = false;

    @Override
    public boolean isProxyEntity()
    {
        return proxy;
    }

    @Override
    public void setProxyEntity(boolean value)
    {
        this.proxy = value;
    }
}
