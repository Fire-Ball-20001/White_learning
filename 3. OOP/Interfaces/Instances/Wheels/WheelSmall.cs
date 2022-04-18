using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Interfaces.interfaces;

namespace Interfaces.Instances.Wheels
{
    internal class WheelSmall : Wheel
    {
        public WheelSmall(string name, ICheck check)
            : base(name,max_pressure:3,min_pressure:1, check) { }

        public override void init(IDictionary<string, object> args)
        {
            check.CheckDictionary(args, new string[]
            {
                "pressure"
            });
            base.init(args);
        }
    }
}
