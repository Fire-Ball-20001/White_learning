using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Interfaces.interfaces;

namespace Interfaces.Instances.Engines
{
    internal class BoatEngine : IEngine
    {
        string name;
        int power;
        ICheck check;

        public BoatEngine(string name,ICheck check)
        {
            this.name = name;
            this.check = check;
        }

        public int GetMaxPower()
        {
            return Int32.MaxValue;
        }

        public int GetMinPower()
        {
            return Int32.MinValue;
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
            return "";
        }

        public void init(IDictionary<string, object> args)
        {
            check.CheckDictionary(args, new string[] { "power" });
            power = (int)args["power"];
        }
    }
}
