using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Interfaces.interfaces;

namespace Interfaces.Instances.Wheels
{
    internal class WheelBig : Wheel
    {
        
        public WheelBig(string name, ICheck check) 
            : base(name,max_pressure:14,min_pressure:9, check) {}

        public override void init(IDictionary<string,object> args)
        {
            check.CheckDictionary(args, new string[]
            {
                "pressure"
            });
            base.init(args);
        }
        
    }
}
