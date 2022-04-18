using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Interfaces.interfaces;

namespace Interfaces.Instances.Bottoms
{
    internal class Bottom : IBottom
    {
        string name;
        int volume;
        string status;
        protected ICheck check;

        public Bottom(string name, ICheck check)
        {
            this.name = name;
            this.check = check;
            status = "";
        }
        public string GetName()
        {
            return name;
        }

        public string GetStatus()
        {
            return status;
        }

        public int GetVolume()
        {
            return volume;
        }

        public virtual void init(IDictionary<string, object> args)
        {
            check.CheckDictionary(args, new string[]
            {
                "min_volume",
                "max_volume",
                "volume"
            });
            int max_volume = (int)args["max_volume"];
            int min_volume = (int)args["min_volume"];
            this.volume = (int)args["volume"];
            try
            {
                check.CheckBottom(max_volume, min_volume, volume);
            }
            catch (Exception ex)
            {
                status = ex.Message;
            }
        }
    }
}
