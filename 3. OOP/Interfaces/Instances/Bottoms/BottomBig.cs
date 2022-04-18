using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Interfaces.interfaces;

namespace Interfaces.Instances.Bottoms
{
    internal class BottomBig : Bottom
    {
        public BottomBig(string name, ICheck check)
            : base(name, check) { }

        public override void init(IDictionary<string, object> args)
        {
            check.CheckDictionary(args, new string[] { "volume" });
            args["max_volume"] = 15;
            args["min_volume"] = 10;
            base.init(args);
        }
    }
}
