using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Interfaces.interfaces;

namespace Interfaces.Instances.Engines
{
    internal class EnginePowerful : Engine
    {
        public EnginePowerful(string name, ICheck check)
           : base(name,max_power: 12000,min_power:6000, check) {}


        public override void init(IDictionary<string, object> args)
        {
            check.CheckDictionary(args, new string[] { "power" });
            base.init(args);
        }
    }
}
