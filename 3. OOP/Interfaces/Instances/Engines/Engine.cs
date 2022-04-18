using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Interfaces.interfaces;

namespace Interfaces.Instances.Engines
{
    internal class Engine : IEngine
    {
        int power;
        string name;
        string status;
        protected ICheck check;
        protected int max_power;
        protected int min_power;

        public Engine(string name,int max_power,int min_power, ICheck check)
        {
            status = "";
            this.name = name;
            this.check = check;
            this.max_power = max_power;
            this.min_power = min_power;
        }
        public string GetName()
        {
            return name;
        }

        public int GetPower()
        {
            return power;
        }

        public string GetStatus()
        {
            return status;
        }

        public virtual void init(IDictionary<string, object> args)
        {
            check.CheckDictionary(args, new string[] { 
                "power"
            });
            int power = (int) args["power"];
            try
            {
                check.CheckEngine(max_power, min_power, power);
            }
            catch(Exception e)
            {
                status = e.Message;
            }
            this.power = power;
            
        }

        public int GetMaxPower()
        {
            return max_power;
        }

        public int GetMinPower()
        {
            return min_power;
        }

    }
}
