using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Interfaces.interfaces;

namespace Interfaces.Instances.Bottoms
{
    internal class BottomSmall : Bottom
    {
        public BottomSmall(string name, ICheck check)
            : base(name, check) { }

        public override void init(IDictionary<string, object> args)
        {
            check.CheckDictionary(args, new string[] { "volume" });
            args["max_volume"] = 5;
            args["min_volume"] = 3;
            base.init(args);
        }
    }
}
