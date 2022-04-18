using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Interfaces.interfaces
{
    internal interface IEngine : BaseInterface
    {
        int GetPower();
        int GetMaxPower();
        int GetMinPower();
    }
}
